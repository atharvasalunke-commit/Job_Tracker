// ============================================================
// Constants and element references
// ============================================================
var TOKEN_KEY = "jobTrackerToken";
var ROLE_KEY = "jobTrackerRole";

var authView = document.querySelector("#auth-view");
var appView = document.querySelector("#app-view");
var authMessage = document.querySelector("#auth-message");
var appMessage = document.querySelector("#app-message");
var jobsList = document.querySelector("#jobs-list");

var pageSizeSelect = document.querySelector("#page-size");
var previousPageButton = document.querySelector("#previous-page");
var nextPageButton = document.querySelector("#next-page");
var pageIndicator = document.querySelector("#page-indicator");

var jobSiteSelect = document.querySelector("#job-site");
var jobTypeSelect = document.querySelector("#job-type");
var jobTypeHint = document.querySelector("#job-type-hint");

var currentPage = 0;


// ============================================================
// Basic helpers
// ============================================================

// Reads the saved JWT. This is the only place that reads it directly -
// every other function calls this function instead.
function getToken() {
  return localStorage.getItem(TOKEN_KEY);
}

function showMessage(element, text, isSuccess) {
  if (isSuccess === undefined) {
    isSuccess = false;
  }
  element.textContent = text;
  if (isSuccess) {
    element.classList.add("success");
  } else {
    element.classList.remove("success");
  }
}

function clearMessage(element) {
  showMessage(element, "", false);
}

// Reads every <input name="..."> inside a <form> into a plain object.
// Example: a form with <input name="username"> and <input name="password">
// becomes { username: "...", password: "..." }
function getFormValues(form) {
  var values = {};
  var formData = new FormData(form);
  var entries = formData.entries();

  var next = entries.next();
  while (!next.done) {
    var key = next.value[0];
    var value = next.value[1];
    values[key] = value;
    next = entries.next();
  }

  return values;
}

// "Java, Spring Boot, MySQL"  ->  ["Java", "Spring Boot", "MySQL"]
function splitSkills(text) {
  var pieces = text.split(",");
  var result = [];

  for (var i = 0; i < pieces.length; i++) {
    var trimmed = pieces[i].trim();
    if (trimmed.length > 0) {
      result.push(trimmed);
    }
  }

  return result;
}


// ============================================================
// The one function every API call goes through.
// It attaches the JWT to the request and checks the response
// for success/failure, so no other function has to repeat that logic.
// ============================================================
async function callApi(path, options) {
  if (!options) {
    options = {};
  }

  var headers = {};
  headers["Content-Type"] = "application/json";

  // copy over any extra headers the caller passed in (currently unused,
  // but kept so nothing breaks if a future call needs a custom header)
  if (options.headers) {
    for (var headerName in options.headers) {
      headers[headerName] = options.headers[headerName];
    }
  }

  var savedToken = getToken();
  if (savedToken) {
    headers["Authorization"] = "Bearer " + savedToken;
  }

  var response = await fetch(path, {
    method: options.method,
    headers: headers,
    body: options.body
  });

  var contentType = response.headers.get("content-type");
  if (!contentType) {
    contentType = "";
  }

  var responseBody;
  if (contentType.indexOf("application/json") !== -1) {
    responseBody = await response.json();
  } else {
    responseBody = await response.text();
  }

  if (response.ok) {
    return responseBody;
  }

  // From here down: the request failed, figure out an error message.

  if (response.status === 401) {
    // Our token is missing, invalid, or expired - the backend rejected us.
    logout("Your session has expired. Please sign in again.");
  }

  var errorMessage;
  if (typeof responseBody === "string") {
    errorMessage = responseBody;
  } else {
    // Validation errors come back as { fieldName: "problem", ... } -
    // collect every message into one readable line.
    var messages = [];
    for (var fieldName in responseBody) {
      messages.push(responseBody[fieldName]);
    }
    errorMessage = messages.join(", ");
  }

  if (!errorMessage) {
    errorMessage = "Request failed (" + response.status + ")";
  }

  throw new Error(errorMessage);
}


// ============================================================
// Auth screen: switching tabs, showing/hiding the app
// ============================================================

function switchAuthTab(tabName) {
  var tabButtons = document.querySelectorAll("[data-auth-tab]");
  for (var i = 0; i < tabButtons.length; i++) {
    var button = tabButtons[i];
    if (button.dataset.authTab === tabName) {
      button.classList.add("active");
    } else {
      button.classList.remove("active");
    }
  }

  var loginForm = document.querySelector("#login-form");
  var registerForm = document.querySelector("#register-form");

  if (tabName === "login") {
    loginForm.classList.remove("hidden");
    registerForm.classList.add("hidden");
  } else {
    loginForm.classList.add("hidden");
    registerForm.classList.remove("hidden");
  }

  clearMessage(authMessage);
}

async function showApp() {
  authView.classList.add("hidden");
  appView.classList.remove("hidden");

  // The role is only used here to decide what the UI shows.
  // The backend independently enforces this with role checks of its own -
  // hiding a button here does not make an endpoint secure by itself.
  var role = localStorage.getItem(ROLE_KEY);
  var isAdmin = (role === "ADMIN");

  var adminPanel = document.querySelector(".admin-panel");
  var dashboardGrid = document.querySelector(".dashboard-grid");

  if (isAdmin) {
    adminPanel.classList.remove("hidden");
    dashboardGrid.classList.remove("single-column");
  } else {
    adminPanel.classList.add("hidden");
    dashboardGrid.classList.add("single-column");
  }

  await loadJobs(true);
}

function logout(message) {
  localStorage.removeItem(TOKEN_KEY);
  localStorage.removeItem(ROLE_KEY);

  appView.classList.add("hidden");
  authView.classList.remove("hidden");

  if (message) {
    showMessage(authMessage, message, false);
  }
}


// ============================================================
// Job-site / job-type dropdown behavior
// ============================================================

function updateJobTypeOptions() {
  var isAuthenticJobs = (jobSiteSelect.value === "authenticjobs");
  var internshipOption = jobTypeSelect.querySelector("option[value='internship']");

  internshipOption.disabled = isAuthenticJobs;

  if (isAuthenticJobs) {
    jobTypeSelect.value = "job";
    jobTypeHint.textContent = "Authentic Jobs is searched as job listings.";
  } else {
    jobTypeHint.textContent = "";
  }
}


// ============================================================
// Event handlers - one named function per form/button, so the
// event-listener list below just says "on click, do this thing"
// ============================================================

async function handleLoginSubmit(event) {
  event.preventDefault();
  clearMessage(authMessage);

  try {
    var formValues = getFormValues(event.currentTarget);
    var response = await callApi("/api/account/Login", {
      method: "POST",
      body: JSON.stringify(formValues)
    });

    localStorage.setItem(TOKEN_KEY, response.token);
    localStorage.setItem(ROLE_KEY, response.role);
    await showApp();
  } catch (error) {
    showMessage(authMessage, error.message, false);
  }
}

async function handleRegisterSubmit(event) {
  event.preventDefault();
  clearMessage(authMessage);

  try {
    var formValues = getFormValues(event.currentTarget);
    var response = await callApi("/api/account/Register", {
      method: "POST",
      body: JSON.stringify(formValues)
    });

    localStorage.setItem(TOKEN_KEY, response.token);
    localStorage.setItem(ROLE_KEY, response.role);
    await showApp();
  } catch (error) {
    showMessage(authMessage, error.message, false);
  }
}

function handleLogoutClick() {
  logout("");
}

async function handleScrapeSubmit(event) {
  event.preventDefault();
  clearMessage(appMessage);

  var submitButton = event.currentTarget.querySelector("button[type='submit']");
  if (submitButton.disabled) {
    return; // a scrape is already in progress, ignore this click
  }

  var originalLabel = submitButton.textContent;
  submitButton.disabled = true;
  submitButton.classList.add("is-loading");
  submitButton.textContent = "Scraping…";

  // The form has a "skills" text field, but the backend expects
  // "user_skills" as a list - convert before sending.
  var formValues = getFormValues(event.currentTarget);
  formValues.user_skills = splitSkills(formValues.skills);
  delete formValues.skills;

  try {
    var savedJobs = await callApi("/api/config/scrape", {
      method: "POST",
      body: JSON.stringify(formValues)
    });

    var message;
    if (savedJobs.length > 0) {
      message = savedJobs.length + " application(s) saved.";
    } else {
      message = "No new applications were saved. They may already be in your list.";
    }

    showMessage(appMessage, message, true);
    await loadJobs(true);
  } catch (error) {
    showMessage(appMessage, error.message, false);
  }

  submitButton.disabled = false;
  submitButton.classList.remove("is-loading");
  submitButton.textContent = originalLabel;
}

async function handleCreateSubmit(event) {
  event.preventDefault();

  try {
    var formValues = getFormValues(event.currentTarget);
    await callApi("/api/jobs", {
      method: "POST",
      body: JSON.stringify(formValues)
    });

    event.currentTarget.reset();
    showMessage(appMessage, "Application saved.", true);
    await loadJobs(true);
  } catch (error) {
    showMessage(appMessage, error.message, false);
  }
}

async function handleConfigSubmit(event) {
  event.preventDefault();

  try {
    var formValues = getFormValues(event.currentTarget);
    await callApi("/api/config/dom_patterns", {
      method: "POST",
      body: JSON.stringify(formValues)
    });

    showMessage(appMessage, "Scraper configuration saved.", true);
  } catch (error) {
    showMessage(appMessage, error.message, false);
  }
}

function handleRefreshClick() {
  loadJobs(false);
}

async function handlePreviousPageClick() {
  if (currentPage === 0) {
    return;
  }
  currentPage = currentPage - 1;
  await loadJobs(false);
}

async function handleNextPageClick() {
  currentPage = currentPage + 1;
  var jobsOnThisPage = await loadJobs(false);

  // If the "next" page came back empty, we've gone past the end -
  // step back and reload the last real page instead.
  if (jobsOnThisPage === 0) {
    currentPage = currentPage - 1;
    await loadJobs(false);
  }
}

function handleJobSiteChange() {
  updateJobTypeOptions();
}

function handleAuthTabClick(event) {
  var tabName = event.currentTarget.dataset.authTab;
  switchAuthTab(tabName);
}


// ============================================================
// Loading and rendering the job list
// ============================================================

// Fetches one page of jobs and renders it.
// Returns how many jobs came back, so the "next page" button
// can tell whether it went past the end of the list.
async function loadJobs(resetToFirstPage) {
  if (resetToFirstPage) {
    currentPage = 0;
  }

  jobsList.innerHTML = '<p class="empty">Loading applications…</p>';

  try {
    var pageSize = Number(pageSizeSelect.value);
    var url = "/api/jobs?page=" + currentPage + "&size=" + pageSize;
    var jobs = await callApi(url, { method: "GET" });

    pageIndicator.textContent = "Page " + (currentPage + 1);
    previousPageButton.disabled = (currentPage === 0);
    nextPageButton.disabled = (jobs.length < pageSize); // fewer than a full page = probably the last page

    if (jobs.length === 0) {
      jobsList.innerHTML = '<p class="empty">No saved applications yet.</p>';
      return 0;
    }

    // Remove whatever cards were there before, then add one card per job.
    jobsList.innerHTML = "";
    for (var i = 0; i < jobs.length; i++) {
      var card = buildJobCard(jobs[i]);
      jobsList.appendChild(card);
    }

    return jobs.length;

  } catch (error) {
    jobsList.innerHTML = '<p class="empty">Could not load applications.</p>';
    showMessage(appMessage, error.message, false);
    return 0;
  }
}

// Builds one job card as a DOM element: title, company, link,
// status badge, a status dropdown, and a delete button.
function buildJobCard(job) {
  var card = document.createElement("article");
  card.className = "job-card";

  // --- left side: job details ---
  var content = document.createElement("div");

  var title = document.createElement("h3");
  title.textContent = job.job_title;

  var company = document.createElement("p");
  company.className = "job-meta";
  company.textContent = job.company_name;

  var link = document.createElement("a");
  link.href = job.application_url;
  link.target = "_blank";
  link.rel = "noreferrer";
  link.textContent = "Open application ↗";

  var badge = document.createElement("span");
  badge.className = "badge";
  badge.textContent = job.status;

  content.appendChild(title);
  content.appendChild(company);
  content.appendChild(link);
  content.appendChild(document.createElement("br"));
  content.appendChild(badge);

  // --- right side: status dropdown + delete button ---
  var actions = document.createElement("div");
  actions.className = "job-actions";

  var statusSelect = document.createElement("select");
  var statusOptions = ["Not applied", "APPLIED", "INTERVIEW", "OFFER", "REJECTED"];

  for (var i = 0; i < statusOptions.length; i++) {
    var statusValue = statusOptions[i];
    var option = document.createElement("option");
    option.value = statusValue;
    option.textContent = statusValue;
    if (job.status.toLowerCase() === statusValue.toLowerCase()) {
      option.selected = true;
    }
    statusSelect.appendChild(option);
  }

  statusSelect.addEventListener("change", async function (event) {
    var newStatus = event.target.value;

    try {
      await callApi("/api/jobs/" + job.id, {
        method: "PUT",
        body: JSON.stringify({
          company_name: job.company_name,
          job_title: job.job_title,
          status: newStatus,
          application_url: job.application_url
        })
      });

      badge.textContent = newStatus;
      showMessage(appMessage, "Status updated!", true);
    } catch (error) {
      statusSelect.value = job.status; // revert the dropdown on failure
      showMessage(appMessage, error.message, false);
    }
  });

  var deleteButton = document.createElement("button");
  deleteButton.textContent = "Delete";
  deleteButton.className = "delete-button";
  deleteButton.addEventListener("click", function () {
    deleteJob(job.id);
  });

  actions.appendChild(statusSelect);
  actions.appendChild(deleteButton);

  card.appendChild(content);
  card.appendChild(actions);

  return card;
}

async function deleteJob(id) {
  var confirmed = confirm("Delete this application?");
  if (!confirmed) {
    return;
  }

  try {
    await callApi("/api/jobs/softdelete/" + id, { method: "PUT" });
    showMessage(appMessage, "Application deleted.", true);
    loadJobs(false);
  } catch (error) {
    showMessage(appMessage, error.message, false);
  }
}


// ============================================================
// Wiring: attach every event handler to its element.
// Reading this block top to bottom tells you every action the
// page can trigger, in one place.
// ============================================================

var authTabButtons = document.querySelectorAll("[data-auth-tab]");
for (var i = 0; i < authTabButtons.length; i++) {
  authTabButtons[i].addEventListener("click", handleAuthTabClick);
}

jobSiteSelect.addEventListener("change", handleJobSiteChange);
updateJobTypeOptions(); // run once immediately so the hint is correct on first load

document.querySelector("#login-form").addEventListener("submit", handleLoginSubmit);
document.querySelector("#register-form").addEventListener("submit", handleRegisterSubmit);
document.querySelector("#logout-button").addEventListener("click", handleLogoutClick);

document.querySelector("#scrape-form").addEventListener("submit", handleScrapeSubmit);
document.querySelector("#create-form").addEventListener("submit", handleCreateSubmit);
document.querySelector("#config-form").addEventListener("submit", handleConfigSubmit);

document.querySelector("#refresh-button").addEventListener("click", handleRefreshClick);
pageSizeSelect.addEventListener("change", function () { loadJobs(true); });
previousPageButton.addEventListener("click", handlePreviousPageClick);
nextPageButton.addEventListener("click", handleNextPageClick);


// ============================================================
// Page start-up: if we already have a saved token, skip the
// login screen and go straight into the app.
// ============================================================
if (getToken()) {
  showApp();
}