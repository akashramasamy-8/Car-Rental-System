// Navbar scroll behavior
let navbar = document.querySelector(".nav-bar");
window.addEventListener("scroll", function () {
    if (window.scrollY > 600) {
        navbar.style.height = "13vh";
        navbar.style.borderBottom = "1px solid white";
    } else if (window.scrollY < 500) {
        navbar.style.height = "10vh";
        navbar.style.borderBottom = "none";
    }
});

// Handling link click and their pseudo-elements
let onelink = document.querySelector(".onelink");
let style_of_onelink = document.styleSheets[0].cssRules[12];

let twolink = document.querySelector(".twolink");
let style_of_twolink = document.styleSheets[0].cssRules[14];

let threelink = document.querySelector(".threelink");
let style_of_threelink = document.styleSheets[0].cssRules[16];

let statusoflink = 0;

onelink.addEventListener("click", function () {
    lineconnection(onelink, style_of_onelink);
});

twolink.addEventListener("click", function () {
    lineconnection(twolink, style_of_twolink);
});

threelink.addEventListener("click", function () {
    lineconnection(threelink, style_of_threelink);
});

// Line connection function for links
function lineconnection(link, pseudo) {
    pseudo.style.transform = "scale(1)";
    link.classList.add("active");

    // Remove the scaling effect reset after 2 seconds to avoid disappearing behavior
    setTimeout(function () {
        pseudo.style.transform = "scale(1)"; // Keep the scale at 1 to prevent disappearance
    }, 2000);
}

// Typing effect for paragraphs
let firstpara = document.querySelector(".pfpara");
let secondpara = document.querySelector(".pspara");
let thirdpara = document.querySelector(".ptpara");

let s1 = "CARCADDY is now open here in India!";
let s2 = "Rent smarter, not harder.";
let s3 = "Freedom on demand. Book now.";

let objfile = [[firstpara, s1], [secondpara, s2], [thirdpara, s3]];

objfile.forEach((item) => {
    let sel = item[0];
    let text = item[1];
    callprinter(sel, text);
});

function callprinter(sel, text) {
    var typed = new Typed(sel, {
        strings: [text],
        typeSpeed: 50,
    });
}

// To reload the Page
function reload() {
    window.location.reload();
}

// Button hover effects
let start = document.querySelector(".Start_today");
let register = document.querySelector(".registration");

start.addEventListener("mouseover", () => {
    start.style.backgroundColor = "#301F47";
    start.style.color = "#ffffff";
    register.style.backgroundColor = "#D9D9D9";
    register.style.color = "#111111";
});

start.addEventListener("mouseleave", () => {
    start.style.backgroundColor = "#D9D9D9";
    start.style.color = "#111111";
    register.style.backgroundColor = "#301F47";
    register.style.color = "#ffffff";
});

register.addEventListener("mouseover", () => {
    start.style.backgroundColor = "#301F47";
    start.style.color = "#ffffff";
    register.style.backgroundColor = "#D9D9D9";
    register.style.color = "#111111";
});

register.addEventListener("mouseleave", () => {
    start.style.backgroundColor = "#D9D9D9";
    start.style.color = "#111111";
    register.style.backgroundColor = "#301F47";
    register.style.color = "#ffffff";
});


// Initialize QRious
const qr = new QRious({
    element: document.getElementById('qr-code'),
    value: 'images/PetCarePal_Brochure.pdf', // URL to the brochure
    size: 200, // Size of the QR code
    level: 'H', // Error correction level (H = high)
  });