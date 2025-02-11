let currentIndex = 0;
const slides = document.querySelectorAll(".banner-container img");
const totalSlides = slides.length;
const bannerContainer = document.querySelector(".banner-container");

function showSlide(index) {
    if (index < 0) {
        currentIndex = totalSlides - 1;
    } else if (index >= totalSlides) {
        currentIndex = 0;
    } else {
        currentIndex = index;
    }
    
    const newTransformValue = -currentIndex * 100 + "%";
    bannerContainer.style.transform = `translateX(${newTransformValue})`;
}

function nextSlide() {
    showSlide(currentIndex + 1);
}

function prevSlide() {
    showSlide(currentIndex - 1);
}

// Auto slide every 4 seconds
setInterval(nextSlide, 10000);

function logout() {
    alert("Logging out...");
}

