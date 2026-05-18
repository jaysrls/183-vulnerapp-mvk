// globals (yay vanilla javascript ftw)
loginCheck();
document.getElementById("login-form")
    .addEventListener("submit", onLoginSubmit);
document.getElementById("logout-form")
    .addEventListener("submit", onLogoutSubmit);
document.getElementById("blog-form")
    .addEventListener("submit", onBlogSubmit);
let devToast = new bootstrap.Toast(
    document.getElementById("devToast"),
    { delay: 10000 }
);

function onLoginSubmit(event) {
  const username = event.target[0].value;
  const password = event.target[1].value;
  event.preventDefault();
  fetch("/api/user/login", {
    method: "POST",
    credentials: "same-origin",
    headers: {
      "Content-Type": "application/x-www-form-urlencoded",
    },
    body: new URLSearchParams({username, password}),
  })
      .then(filterOk)
      .then(response => response.json())
      .then(user => window.sessionStorage.setItem("fullname", user.fullname))
      .then(() => loginCheck());
}

function onLogoutSubmit(event) {
  event.preventDefault();
  fetch("/api/user/logout", { method: "POST", credentials: "same-origin" })
      .then(filterOk)
      .then(() => {
        window.sessionStorage.removeItem("fullname");
        loginCheck();
      });
}

function onBlogSubmit(event) {
  const data = {"title": event.target[0].value, "body": event.target[1].value};
  event.preventDefault();
  fetch("/api/blog", {
    method: "POST",
    credentials: "same-origin",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(data),
  })
      .then(filterOk)
      .then(() => fetchBlogs())
      .then(() => event.target.reset());
}

// switch display based on login status
function loginCheck() {
  fetch("/api/user/whoami", { credentials: "same-origin" })
      .then(response => {
        if (!response.ok) {
          throw response;
        }
        return response.json();
      })
      .then(user => {
        window.sessionStorage.setItem("fullname", user.fullname);
        renderLoginState(user.fullname);
        fetchBlogs();
      })
      .catch(() => {
        window.sessionStorage.removeItem("fullname");
        renderLoginState("anonymous");
        renderBlogs([]);
      });
}

function renderLoginState(fullname) {
  const authentic = fullname !== "anonymous";
  document.getElementById("login-form").parentElement.hidden = authentic;
  document.getElementById("logout-form").parentElement.hidden = !authentic;
  document.getElementById("username").innerText = fullname;
}

function fetchBlogs() {
  fetch("/api/blog", { credentials: "same-origin" })
      .then(filterOk)
      .then(response => response.json())
      .then(page => renderBlogs(page.content));
}

function renderBlogs(blogs) {
  const blogDiv = document.getElementById("blog-container");
  blogDiv.innerHTML = "" // clear
  for (const blog of blogs) {
    blogDiv.innerHTML += `<h2>${blog.title}</h2>
            <p>${blog.createdAt}</p>
            <p>${blog.body}</p>`;
  }
}

function showDevError(message) {
  document.getElementById("devToastText").textContent = message;
  devToast.show();
}

function filterOk(response) {
  if (response.ok) {
    return response;
  }
  return response.text().then(function(bodyText) {
    let msg = `HTTP ${response.status} ${response.statusText}\n${bodyText}`;
    if(msg.length > 1000){
      msg = msg.substring(0, 1000) + "\n...[truncated]";
    }
    showDevError(msg);
    throw response;
  });
}
