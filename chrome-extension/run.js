const MAX_PARTICIPANTS = 100;

function registerCustomCSS() {
  items = document.getElementsByClassName("points");
  for (var i = 0; i < items.length; i++) {
    items[i].id = `points-${i}`;
  }
}

function registerStyleInHtml() {
  var style = document.createElement("style");
  html = "";
  for (var i = 0; i < MAX_PARTICIPANTS; i++) {
    html += `
    #points-${i} {
      font-size: 18px !important;
    }
    `;
  }
  style.innerHTML = html;
  document.head.appendChild(style);
}

function registerEventListeners() {
  $("#tools a").click(function (e) {
    e.preventDefault();
    registerCustomCSS();
  });
}

function start() {
  setInterval(() => {
    registerCustomCSS();
  }, 500);

  registerStyleInHtml();
}

document.onload = start();
