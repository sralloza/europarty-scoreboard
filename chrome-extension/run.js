function registerCustomCSS() {
  var style = document.createElement("style");
  html = "";
  items = document.getElementsByClassName("points");
  for (var i = 0; i < items.length; i++) {
    items[i].id = `points-${i}`;
    html += `
    #points-${i} {
      font-size: 18px !important;
    }
    `;
  }
  console.log(html);
  style.innerHTML = html;
  document.head.appendChild(style);
}

document.onload = registerCustomJS();
