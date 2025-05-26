function showPopup() {
    document.getElementById('popupOverlay').classList.remove('d-none');
}

function hidePopup() {
    const popupOverlay = document.getElementById('popupOverlay');
    const popupForm = popupOverlay.querySelector('.popup-form');

    // Add hide animation class
    popupForm.classList.add('popupHide');
    popupOverlay.classList.add('popupOverlayHide'); // fade out overlay

    // After animation ends, hide overlay and reset class
    popupForm.addEventListener('animationend', () => {
        popupOverlay.classList.add('d-none');
        popupForm.classList.remove('popupHide');
        popupOverlay.classList.remove('popupOverlayHide');
    }, { once: true });
}
// Close popup when clicking on the dark overlay (but not the form)
document.addEventListener('DOMContentLoaded', () => {
    const popupOverlay = document.getElementById('popupOverlay');
    const popupForm = document.querySelector('.popup-form');

    popupOverlay.addEventListener('click', (e) => {
        if (!popupForm.contains(e.target)) {
            hidePopup();
        }
    });
});

document.addEventListener('click', function (e) {
  const dropdown = document.getElementById('sortDropdown');
  const isDropdownArea = e.target.closest('.dropdown-custom');

  // If clicked outside .dropdown-custom, close the dropdown
  if (!isDropdownArea && !dropdown.classList.contains('d-none')) {
    dropdown.classList.add('d-none');
  }
});

function toggleDetails(button) {
    const jobCard = button.closest('.job-card');
    const detailsDiv = jobCard.querySelector('.job-details');
    const accChart = jobCard.querySelector('.accuracy-chart');
    const lossChart = jobCard.querySelector('.loss-chart');

    if (detailsDiv.classList.contains('d-none')) {
        detailsDiv.classList.remove('d-none');
        button.textContent = '▲';
        drawCharts(accChart, lossChart, exampleData);
    } else {
        detailsDiv.classList.add('d-none');
        button.textContent = '▼';
    }
}

let accuracyChart = null;
let lossChart = null;

function drawCharts(accCanvas, lossCanvas, newData) {
    // newData is an object with updated datasets, or generate new random data

    if (!accuracyChart) {
        accuracyChart = new Chart(accCanvas, {
            type: 'line',
            data: newData.accuracy,
            options: {
                responsive: true,
                plugins: {
                    title: {
                        display: true,
                        text: 'Accuracy over Batches',
                        color: 'white',
                        font: { size: 16 },
                        position: 'bottom',
                        align: 'center'
                    },
                    legend: {
                        labels: { color: '#fff' },
                        position: 'top',
                        align: 'end',
                    }
                },
                scales: {
                    x: {
                        title: {
                            display: true,
                            text: 'Batch Index',
                            color: 'white'
                        },
                        ticks: { color: '#ccc' }
                    },
                    y: {
                        title: {
                            display: true,
                            text: 'Accuracy (%)',
                            color: 'white'
                        },
                        ticks: { color: '#ccc' }
                    }
                }
            }
        });
    } else {
        accuracyChart.data = newData.accuracy;
        accuracyChart.update();
    }

    if (!lossChart) {
        lossChart = new Chart(lossCanvas, {
            type: 'line',
            data: newData.loss,
            options: {
                responsive: true,
                plugins: {
                    title: {
                        display: true,
                        text: 'Loss over Batches',
                        color: 'white',
                        font: { size: 16 },
                        position: 'bottom',
                        align: 'center'
                    },
                    legend: {
                        labels: { color: '#fff' },
                        position: 'top',
                        align: 'end',
                    }
                },
                scales: {
                    x: {
                        title: {
                            display: true,
                            text: 'Batch Index',
                            color: 'white'
                        },
                        ticks: { color: '#ccc' }
                    },
                    y: {
                        title: {
                            display: true,
                            text: 'Loss',
                            color: 'white'
                        },
                        ticks: { color: '#ccc' }
                    }
                }
            }
        });
    } else {
        lossChart.data = newData.loss;
        lossChart.update();
    }
}

// Example usage:
const exampleData = {
    accuracy: {
        labels: [...Array(50).keys()].map(i => i + 1),
        datasets: [
            {
                label: 'Training Accuracy',
                data: Array.from({ length: 50 }, () => Math.random() * 100),
                borderColor: 'orange',
                fill: false,
                tension: 0.3
            },
            {
                label: 'Validation Accuracy',
                data: Array.from({ length: 50 }, () => Math.random() * 100),
                borderColor: 'white',
                fill: false,
                tension: 0.3
            }
        ]
    },
    loss: {
        labels: [...Array(50).keys()].map(i => i + 1),
        datasets: [
            {
                label: 'Training Loss',
                data: Array.from({ length: 50 }, () => Math.random()),
                borderColor: 'orange',
                fill: false,
                tension: 0.3
            },
            {
                label: 'Validation Loss',
                data: Array.from({ length: 50 }, () => Math.random()),
                borderColor: 'white',
                fill: false,
                tension: 0.3
            }
        ]
    }
};

function toggleSortDropdown() {
  const dropdown = document.getElementById('sortDropdown');
  dropdown.classList.toggle('d-none');
}

function sortBy(field) {
  console.log("Sorting by:", field);
  const dropdown = document.getElementById('sortDropdown');
  dropdown.classList.add('d-none');
}