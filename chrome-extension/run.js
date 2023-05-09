function loadJS(FILE_URL, async = true) {
  let scriptEle = document.createElement("script");

  scriptEle.setAttribute("src", FILE_URL);
  scriptEle.setAttribute("type", "text/javascript");
  scriptEle.setAttribute("async", async);

  document.body.appendChild(scriptEle);

  // success event
  scriptEle.addEventListener("load", () => {
    console.log("File loaded");
  });
  // error event
  scriptEle.addEventListener("error", (ev) => {
    console.log("Error on loading file", ev);
  });
}

function registerCustomJS() {
  console.log("Registering custom event manager");
  $("body").keydown(function (e) {
    switch (e.which) {
      case 38:
        console.log("Clicking faster button");
        $("#tools a#faster").click();
        break; // up
      case 40:
        console.log("Clicking slower button");
        $("#tools a#slower").click();
        break; // down
    }
  });
}

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

function waitForJQuery() {
  if (typeof $ !== "undefined") {
    console.debug("jQuery loaded registering custom js");
    registerCustomJS();
    registerCustomCSS();
  } else {
    console.debug("jQuery not loaded, waiting 250ms");
    setTimeout(waitForJQuery, 250);
  }
}
document.onload = waitForJQuery();

// $('#tools a').click(function(e) {
//   e.preventDefault();
//   var action = $(this).attr('id');
//   switch(action) {
//     case 'rewind': sw.rewind(2); break;
//     case 'forward': sw.forward(); break;
//     case 'pause': t.pause(); break;
//     case 'restart': sw.goto(0); break;
//     case 'slower': t.delay+=200; if (t.delay > 3000) { t.delay = 3000; }; break;
//     case 'faster': t.delay-=200; if (t.delay < 400) { t.delay = 400; }; break;
//     case 'help': showInfo(); break;
//   }
// });
