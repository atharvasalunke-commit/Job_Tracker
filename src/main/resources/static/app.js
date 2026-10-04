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

var currentPage = 0;


// ============================================================
// Basic helpers
// ============================================================

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


// ============================================================
// Convert form values into an object
// ============================================================

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


// ============================================================
// Convert skills string into an array
//
// "Java, Spring Boot, MySQL"
// ->
// ["Java", "Spring Boot", "MySQL"]
// ============================================================

function splitSkills(text) {

  if (!text) {
    return [];
  }

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
// API helper
// ============================================================

async function callApi(path, options) {

  if (!options) {
    options = {};
  }

  var headers = {};

  headers["Content-Type"] = "application/json";


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


  if (response.status === 401) {

    logout(
        "Your session has expired. Please sign in again."
    );

  }


  var errorMessage;


  if (typeof responseBody === "string") {

    errorMessage = responseBody;

  } else {

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
// AUTH
// ============================================================

function switchAuthTab(tabName) {

  var tabButtons =
      document.querySelectorAll("[data-auth-tab]");


  for (var i = 0; i < tabButtons.length; i++) {

    var button = tabButtons[i];

    if (button.dataset.authTab === tabName) {

      button.classList.add("active");

    } else {

      button.classList.remove("active");

    }

  }


  var loginForm =
      document.querySelector("#login-form");

  var registerForm =
      document.querySelector("#register-form");


  if (tabName === "login") {

    loginForm.classList.remove("hidden");

    registerForm.classList.add("hidden");

  } else {

    loginForm.classList.add("hidden");

    registerForm.classList.remove("hidden");

  }


  clearMessage(authMessage);
}


// ============================================================
// Show application
// ============================================================

async function showApp() {

  authView.classList.add("hidden");

  appView.classList.remove("hidden");


  var role = localStorage.getItem(ROLE_KEY);

  var isAdmin = (role === "ADMIN");


  var adminPanel =
      document.querySelector(".admin-panel");

  var dashboardGrid =
      document.querySelector(".dashboard-grid");


  if (isAdmin) {

    adminPanel.classList.remove("hidden");

    dashboardGrid.classList.remove("single-column");

  } else {

    adminPanel.classList.add("hidden");

    dashboardGrid.classList.add("single-column");

  }


  await loadJobs(true);
}


// ============================================================
// Logout
// ============================================================

function logout(message) {

  localStorage.removeItem(TOKEN_KEY);

  localStorage.removeItem(ROLE_KEY);


  appView.classList.add("hidden");

  authView.classList.remove("hidden");


  if (message) {

    showMessage(
        authMessage,
        message,
        false
    );

  }

}


// ============================================================
// AUTH EVENT HANDLERS
// ============================================================

async function handleLoginSubmit(event) {

  event.preventDefault();

  clearMessage(authMessage);


  try {

    var formValues =
        getFormValues(event.currentTarget);


    var response =
        await callApi("/api/account/Login", {

          method: "POST",

          body: JSON.stringify(formValues)

        });


    localStorage.setItem(
        TOKEN_KEY,
        response.token
    );

    localStorage.setItem(
        ROLE_KEY,
        response.role
    );


    await showApp();


  } catch (error) {

    showMessage(
        authMessage,
        error.message,
        false
    );

  }

}


async function handleRegisterSubmit(event) {

  event.preventDefault();

  clearMessage(authMessage);


  try {

    var formValues =
        getFormValues(event.currentTarget);


    var response =
        await callApi("/api/account/Register", {

          method: "POST",

          body: JSON.stringify(formValues)

        });


    localStorage.setItem(
        TOKEN_KEY,
        response.token
    );

    localStorage.setItem(
        ROLE_KEY,
        response.role
    );


    await showApp();


  } catch (error) {

    showMessage(
        authMessage,
        error.message,
        false
    );

  }

}


function handleLogoutClick() {
  logout("");
}


// ============================================================
// JOB SCRAPING
// ============================================================

async function handleScrapeSubmit(event) {

  event.preventDefault();

  clearMessage(appMessage);


  var form = event.currentTarget;

  var submitButton =
      form.querySelector("button[type='submit']");


  if (submitButton.disabled) {
    return;
  }


  var originalLabel =
      submitButton.textContent;


  submitButton.disabled = true;

  submitButton.classList.add("is-loading");

  submitButton.textContent = "Searching...";


  try {

    // --------------------------------------------------------
    // Get selected job sites
    // --------------------------------------------------------

    var siteCheckboxes =
        document.querySelectorAll("#job-sites input[name='site_name']:checked");

    var selectedSites = [];

    for (var i = 0; i < siteCheckboxes.length; i++) {
      selectedSites.push(siteCheckboxes[i].value);
    }


    if (selectedSites.length === 0) {

      throw new Error(
          "Select at least one job site."
      );

    }


    // --------------------------------------------------------
    // Get other form values
    // --------------------------------------------------------

    var formValues =
        getFormValues(form);


    // Skills

    var userSkills =
        splitSkills(formValues.skills);


    // Search term

    var searchTerm = null;

    if (
        formValues.search_term &&
        formValues.search_term.trim()
    ) {

      searchTerm =
          formValues.search_term.trim();

    }


    // Location

    var location = null;

    if (
        formValues.location &&
        formValues.location.trim()
    ) {

      location =
          formValues.location.trim();

    }


    // Job type

    var jobType = null;

    if (
        formValues.job_type &&
        formValues.job_type.trim()
    ) {

      jobType =
          formValues.job_type;

    }


    // Remote

    var isRemote = null;


    if (formValues.is_remote === "true") {

      isRemote = true;

    } else if (
        formValues.is_remote === "false"
    ) {

      isRemote = false;

    }


    // --------------------------------------------------------
    // Create request body
    // --------------------------------------------------------

    var requestBody = {

      site_name: selectedSites,

      search_term: searchTerm,

      location: location,

      user_skills: userSkills,

      job_type: jobType,

      is_remote: isRemote

    };


    console.log(
        "Scrape request:",
        requestBody
    );


    // --------------------------------------------------------
    // Send request
    // --------------------------------------------------------

    var savedJobs =
        await callApi(
            "/api/scrape",
            {
              method: "POST",
              body: JSON.stringify(requestBody)
            }
        );


    // --------------------------------------------------------
    // Message
    // --------------------------------------------------------

    if (savedJobs.length > 0) {

      showMessage(
          appMessage,
          savedJobs.length +
          " application(s) saved.",
          true
      );

    } else {

      showMessage(
          appMessage,
          "No new applications were saved.",
          true
      );

    }


    await loadJobs(true);


  } catch (error) {

    showMessage(
        appMessage,
        error.message,
        false
    );

  } finally {

    submitButton.disabled = false;

    submitButton.classList.remove(
        "is-loading"
    );

    submitButton.textContent =
        originalLabel;

  }

}


// ============================================================
// MANUAL APPLICATION
// ============================================================

async function handleCreateSubmit(event) {

  event.preventDefault();


  try {

    var formValues =
        getFormValues(event.currentTarget);


    await callApi(
        "/api/jobs",
        {
          method: "POST",
          body: JSON.stringify(formValues)
        }
    );


    event.currentTarget.reset();


    showMessage(
        appMessage,
        "Application saved.",
        true
    );


    await loadJobs(true);


  } catch (error) {

    showMessage(
        appMessage,
        error.message,
        false
    );

  }

}


// ============================================================
// REFRESH
// ============================================================

function handleRefreshClick() {
  loadJobs(false);
}


// ============================================================
// PAGINATION
// ============================================================

async function handlePreviousPageClick() {

  if (currentPage === 0) {
    return;
  }


  currentPage =
      currentPage - 1;


  await loadJobs(false);
}


async function handleNextPageClick() {

  currentPage =
      currentPage + 1;


  var jobsOnThisPage =
      await loadJobs(false);


  if (jobsOnThisPage === 0) {

    currentPage =
        currentPage - 1;


    await loadJobs(false);

  }

}


// ============================================================
// LOAD JOBS
// ============================================================

async function loadJobs(resetToFirstPage) {

  if (resetToFirstPage) {
    currentPage = 0;
  }


  jobsList.innerHTML =
      '<p class="empty">Loading applications...</p>';


  try {

    var pageSize =
        Number(pageSizeSelect.value);


    var url =
        "/api/jobs?page=" +
        currentPage +
        "&size=" +
        pageSize;


    var jobs =
        await callApi(
            url,
            {
              method: "GET"
            }
        );


    pageIndicator.textContent =
        "Page " +
        (currentPage + 1);


    previousPageButton.disabled =
        (currentPage === 0);


    nextPageButton.disabled =
        (jobs.length < pageSize);


    if (jobs.length === 0) {

      jobsList.innerHTML =
          '<p class="empty">No saved applications yet.</p>';

      return 0;

    }


    jobsList.innerHTML = "";


    for (var i = 0; i < jobs.length; i++) {

      var card =
          buildJobCard(jobs[i]);


      jobsList.appendChild(card);

    }


    return jobs.length;


  } catch (error) {

    jobsList.innerHTML =
        '<p class="empty">Could not load applications.</p>';


    showMessage(
        appMessage,
        error.message,
        false
    );


    return 0;

  }

}


// ============================================================
// BUILD JOB CARD
// ============================================================

function buildJobCard(job) {

  var card =
      document.createElement("article");

  card.className =
      "job-card";


  // ----------------------------------------------------------
  // Job information
  // ----------------------------------------------------------

  var content =
      document.createElement("div");


  var title =
      document.createElement("h3");

  title.textContent =
      job.job_title;


  var company =
      document.createElement("p");

  company.className =
      "job-meta";

  company.textContent =
      job.company_name;


  var link =
      document.createElement("a");

  link.href =
      job.application_url;

  link.target =
      "_blank";

  link.rel =
      "noreferrer";

  link.textContent =
      "Open application ↗";


  var badge =
      document.createElement("span");

  badge.className =
      "badge";

  badge.textContent =
      job.status;


  content.appendChild(title);

  content.appendChild(company);

  content.appendChild(link);

  content.appendChild(
      document.createElement("br")
  );

  content.appendChild(badge);


  // ----------------------------------------------------------
  // Actions
  // ----------------------------------------------------------

  var actions =
      document.createElement("div");

  actions.className =
      "job-actions";


  var statusSelect =
      document.createElement("select");


  var statusOptions = [
    "Not applied",
    "APPLIED",
    "INTERVIEW",
    "OFFER",
    "REJECTED"
  ];


  for (
      var i = 0;
      i < statusOptions.length;
      i++
  ) {

    var statusValue =
        statusOptions[i];


    var option =
        document.createElement("option");


    option.value =
        statusValue;

    option.textContent =
        statusValue;


    if (
        job.status &&
        job.status.toLowerCase() ===
        statusValue.toLowerCase()
    ) {

      option.selected = true;

    }


    statusSelect.appendChild(option);

  }


  statusSelect.addEventListener(
      "change",
      async function (event) {

        var newStatus =
            event.target.value;


        try {

          await callApi(
              "/api/jobs/" + job.id,
              {
                method: "PUT",

                body: JSON.stringify({

                  company_name:
                  job.company_name,

                  job_title:
                  job.job_title,

                  status:
                  newStatus,

                  application_url:
                  job.application_url

                })

              }
          );


          badge.textContent =
              newStatus;


          job.status =
              newStatus;


          showMessage(
              appMessage,
              "Status updated!",
              true
          );


        } catch (error) {

          statusSelect.value =
              job.status;


          showMessage(
              appMessage,
              error.message,
              false
          );

        }

      }
  );


  // ----------------------------------------------------------
  // Delete button
  // ----------------------------------------------------------

  var deleteButton =
      document.createElement("button");


  deleteButton.textContent =
      "Delete";


  deleteButton.className =
      "delete-button";


  deleteButton.type =
      "button";


  deleteButton.addEventListener(
      "click",
      function () {
        deleteJob(job.id);
      }
  );


  actions.appendChild(
      statusSelect
  );

  actions.appendChild(
      deleteButton
  );


  card.appendChild(content);

  card.appendChild(actions);


  return card;
}


// ============================================================
// DELETE JOB
// ============================================================

async function deleteJob(id) {

  var confirmed =
      confirm(
          "Delete this application?"
      );


  if (!confirmed) {
    return;
  }


  try {

    await callApi(
        "/api/jobs/softdelete/" + id,
        {
          method: "PUT"
        }
    );


    showMessage(
        appMessage,
        "Application deleted.",
        true
    );


    loadJobs(false);


  } catch (error) {

    showMessage(
        appMessage,
        error.message,
        false
    );

  }

}


// ============================================================
// EVENT WIRING
// ============================================================


// Authentication tabs

var authTabButtons =
    document.querySelectorAll(
        "[data-auth-tab]"
    );


for (
    var i = 0;
    i < authTabButtons.length;
    i++
) {

  authTabButtons[i].addEventListener(
      "click",
      function (event) {

        switchAuthTab(
            event.currentTarget.dataset.authTab
        );

      }
  );

}


// Authentication forms

document
    .querySelector("#login-form")
    .addEventListener(
        "submit",
        handleLoginSubmit
    );


document
    .querySelector("#register-form")
    .addEventListener(
        "submit",
        handleRegisterSubmit
    );


document
    .querySelector("#logout-button")
    .addEventListener(
        "click",
        handleLogoutClick
    );


// Job scraping

document
    .querySelector("#scrape-form")
    .addEventListener(
        "submit",
        handleScrapeSubmit
    );


// Manual application

document
    .querySelector("#create-form")
    .addEventListener(
        "submit",
        handleCreateSubmit
    );


// Refresh

document
    .querySelector("#refresh-button")
    .addEventListener(
        "click",
        handleRefreshClick
    );


// Pagination

pageSizeSelect.addEventListener(
    "change",
    function () {
      loadJobs(true);
    }
);


previousPageButton.addEventListener(
    "click",
    handlePreviousPageClick
);


nextPageButton.addEventListener(
    "click",
    handleNextPageClick
);


// ============================================================
// PAGE STARTUP
// ============================================================

if (getToken()) {

  showApp();

}