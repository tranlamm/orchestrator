/** ________________ DATA ________________ */
let currentPage = 0;
let currentSortField = null;
let currentIsAscending = null;
let mapModelChart = {};
let mapData = {};
let mapDataDetails = {};

/** ________________ CHANGE PAGE ________________ */
function viewFinishedJob() {
    window.location.href = '/home_done';
}

/** ________________ SHOW POPUP ________________ */
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

/** ________________ SHOW SORT DROPDOWN ________________ */
function toggleSortDropdown() {
    const dropdown = document.getElementById('sortDropdown');
    dropdown.classList.toggle('d-none');
}

function toggleOrderDropdown() {
    const dropdown = document.getElementById('orderDropdown');
    dropdown.classList.toggle('d-none');
}

document.addEventListener('click',  (e) => {
    const dropdown = document.getElementById('sortDropdown');
    const isDropdownArea = e.target.closest('.dropdown-custom1');

    if (!isDropdownArea && !dropdown.classList.contains('d-none')) {
        dropdown.classList.add('d-none');
    }

    const dropdown2 = document.getElementById('orderDropdown');
    const isDropdownArea2 = e.target.closest('.dropdown-custom2');

    if (!isDropdownArea2 && !dropdown.classList.contains('d-none')) {
        dropdown2.classList.add('d-none');
    }
});

function sortBy(field) {
    const dropdown = document.getElementById('sortDropdown');
    dropdown.classList.add('d-none');
    if (currentSortField == field) return;
    currentSortField = field;
    sendRequestData().then(r => refreshPage());
}

function setIsIncrease(isAscending) {
    const dropdown = document.getElementById('orderDropdown');
    dropdown.classList.add('d-none');
    if (currentIsAscending == isAscending) return;
    currentIsAscending = isAscending;
    sendRequestData().then(r => refreshPage());
}

/** ________________ SHOW MODEL DETAIL ________________ */
function toggleDetails(button) {
    const jobCard = button.closest('.job-card');
    const detailsDiv = jobCard.querySelector('.job-details');
    const accChart = jobCard.querySelector('.accuracy-chart');
    const lossChart = jobCard.querySelector('.loss-chart');
    const modelId = jobCard.getAttribute("modelId");

    if (detailsDiv.classList.contains('d-none')) {
        detailsDiv.classList.remove('d-none');
        button.textContent = '▲';
        if (mapDataDetails[modelId] != null) {
            refreshModelDetail(modelId);
        }
        sendRequestModelDetail(modelId).then(r => refreshModelDetail(modelId));
    } else {
        detailsDiv.classList.add('d-none');
        button.textContent = '▼';
    }
}

/** ________________ PAGINATE ________________ */
function changePage(page) {
    if (currentPage == page) return;
    currentPage = page;
    sendRequestData().then(r => refreshPage());
}

function renderPagination(totalPages, currentPage) {
    const container = document.getElementById('pagination');
    container.innerHTML = '';

    const createBtn = (text, page, isActive = false) => {
        const btn = document.createElement('button');
        btn.className = 'pagination-btn';
        if (isActive) btn.classList.add('active');
        btn.innerText = text;
        btn.onclick = () => {
            changePage(page);
        };
        return btn;
    };

    // Prev button
    if (currentPage > 1) {
        container.appendChild(createBtn('Prev', currentPage - 1));
    }

    const pages = [];

    if (totalPages <= 6) {
        for (let i = 1; i <= totalPages; i++) {
            pages.push(i);
        }
    } else {
        pages.push(1);
        pages.push(2);
        if (currentPage > 3) pages.push('...');

        if (currentPage > 2 && currentPage < totalPages - 2) {
            pages.push(currentPage);
            pages.push(currentPage + 1);
        } else if (currentPage == 2) {
            pages.push(currentPage + 1);
        } else if (currentPage == totalPages - 1 || currentPage == totalPages - 2) {
            pages.push(totalPages - 2);
        }

        if (currentPage < totalPages - 3) pages.push('...');
        pages.push(totalPages - 1);
        pages.push(totalPages);
    }

    pages.forEach(p => {
        if (p === '...') {
            const dot = document.createElement('span');
            dot.className = 'pagination-btn disabled';
            dot.innerText = '...';
            container.appendChild(dot);
        } else {
            container.appendChild(createBtn(p, p, p === currentPage));
        }
    });

    if (currentPage < totalPages) {
        container.appendChild(createBtn('Next', currentPage + 1));
    }
}

function drawCharts(accCanvas, lossCanvas, modelId, newData) {
    if (!mapModelChart[modelId]) mapModelChart[modelId] = {};
    if (!mapModelChart[modelId].accChart) {
        mapModelChart[modelId].accChart = new Chart(accCanvas, {
            type: 'line',
            data: newData.accuracy,
            options: {
                responsive: true,
                plugins: {
                    title: {
                        display: true,
                        text: 'Model accuracy',
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
                            text: 'Batch Number',
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
        mapModelChart[modelId].accChart.data = newData.accuracy;
        mapModelChart[modelId].accChart.update();
    }

    if (!mapModelChart[modelId].lossChart) {
        mapModelChart[modelId].lossChart = new Chart(lossCanvas, {
            type: 'line',
            data: newData.loss,
            options: {
                responsive: true,
                plugins: {
                    title: {
                        display: true,
                        text: 'Model loss',
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
                            text: 'Batch Number',
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
        mapModelChart[modelId].lossChart.data = newData.loss;
        mapModelChart[modelId].lossChart.update();
    }
}

/** ________________ FIRST LOAD ________________ */
document.addEventListener('DOMContentLoaded', () => {
    const popupOverlay = document.getElementById('popupOverlay');
    const popupForm = document.querySelector('.popup-form');

    popupOverlay.addEventListener('click', (e) => {
        if (!popupForm.contains(e.target)) {
            hidePopup();
        }
    });

    sendRequestJob().then(r => refreshPage());
});

async function sendRequestJob() {
    try {
        fetch('/api/view/model/training/all', {
            method: 'GET',
            headers: { 'Content-Type': 'application/json' },
        })
        .then(async response => {
            if (response.status == 404) {
                let message = await response.text();
                console.error("Error:", message);
                alert("Unsupported sort field");
            }
            else if (response.status != 200) {
                window.location.href = '/login';
            } else {
                const result = await response.json();
                saveData(result);
            }
        })
        .catch(err => {
            console.error("Error:", err);
            alert("Something went wrong");
        });
    }
    catch (err) {
        console.error("Error:", err);
        alert("Something went wrong");
    }
}

async function sendRequestData() {
    let pageIdx = currentPage;
    let sortField = currentSortField;
    let isAscending = currentIsAscending;
    let query = "";
    if (sortField) {
        if (isAscending == null) {
            currentIsAscending = true;
            isAscending = currentIsAscending;
        }
        query = "?sortedField=" + sortField + "&ascending=" + isAscending + "&page=" + pageIdx;
    } else {
        query = "?page=" + pageIdx;
    }
    try {
        fetch('/api/view/model/training/all' + query, {
            method: 'GET',
            headers: { 'Content-Type': 'application/json' },
        })
            .then(async response => {
                if (response.status != 200) {
                    let message = await response.text();
                    console.error("Error:", message);
                    alert("Unsupported sort field");
                }
                else {
                    const result = await response.json();
                    saveData(result);
                }
            })
            .catch(err => {
                console.error("Error:", err);
                alert("Something went wrong");
            });
    }
    catch (err) {
        console.error("Error:", err);
        alert("Something went wrong");
    }
}

function saveData(data) {
    mapData.numPage = data.numPage;
    mapData.pageIdx = data.pageIdx;
    mapData.sizePerPage = data.sizePerPage;

    mapData.info = {};
    for (let res of data.results) {
        let modelId = res.modelId;
        mapData.info[modelId] = res;
    }
}

function refreshPage() {
    let jobContainer = document.getElementById("job_card_container");
    jobContainer.innerHTML = '';
    let listModelId = Object.keys(mapData.info);
    for (let modelId of listModelId) {
        let card = createJobCard(modelId);
        jobContainer.appendChild(card);
    }

    let pageIdx = mapData.pageIdx;
    let totalPage = mapData.numPage;
    currentPage = pageIdx;
    renderPagination(totalPage, pageIdx);
}

function createJobCard(modelId) {
    // Main container
    const card = document.createElement('div');
    card.classList.add('job-card', 'animate__animated', 'animate__fadeIn');
    card.id = 'job-card_' + modelId;
    card.setAttribute("modelId", modelId);

    // Inner top structure
    const topContainer = document.createElement('div');

    const header = document.createElement('div');
    header.classList.add('job-header');
    const strongId = document.createElement('strong');
    strongId.textContent = 'ID: ' + modelId;
    header.appendChild(strongId);

    // Job description row
    const row = document.createElement('div');
    row.classList.add('d-flex', 'justify-content-between', 'align-items-center');

    const description = document.createElement('div');
    description.classList.add('job-description');
    const spans = [
        ['acc', 'Accuracy: ' + mapData.info[modelId].currentAccuracy],
        ['loss', 'Loss: ' + mapData.info[modelId].currentLoss],
        ['epoch', 'Epoch: ' + mapData.info[modelId].currentEpochIdx + "/" + mapData.info[modelId].totalEpoch],
        ['batch', 'Batch: ' + mapData.info[modelId].currentBatchIdx + "/" + mapData.info[modelId].numBatchPerEpoch],
        ['started', 'Started time: ' + formatTimestamp(mapData.info[modelId].startTime)],
    ];
    spans.forEach(arr => {
        const span = document.createElement('span');
        span.id = "job-description_" + arr[0] + "_" + modelId;
        span.textContent = arr[1];
        description.appendChild(span);
    });

    const toggleBtn = document.createElement('button');
    toggleBtn.classList.add('btn', 'btn-sm', 'btn-orange');
    toggleBtn.textContent = '▼';
    toggleBtn.onclick = () => {
        toggleDetails(toggleBtn);
    };

    row.appendChild(description);
    row.appendChild(toggleBtn);

    topContainer.appendChild(header);
    topContainer.appendChild(row);

    // Detail section
    const details = document.createElement('div');
    details.classList.add('job-details', 'mt-3', 'd-none');
    details.id = "job-details_" + modelId;

    const detailRow = document.createElement('div');
    detailRow.classList.add('d-flex', 'gap-3');

    // Left details
    const left = document.createElement('div');
    left.classList.add('flex-fill', 'left-details');
    left.style.flex = '3';

    // Right side (charts)
    const right = document.createElement('div');
    right.classList.add('flex-fill');
    right.style.flex = '7';

    const accuracyChart = document.createElement('canvas');
    accuracyChart.classList.add('accuracy-chart', 'mb-5');
    accuracyChart.height = 150;
    accuracyChart.id = "accuracyChart_" + modelId;

    const lossChart = document.createElement('canvas');
    lossChart.classList.add('loss-chart');
    lossChart.height = 150;
    lossChart.id = "lossChart_" + modelId;

    right.appendChild(accuracyChart);
    right.appendChild(lossChart);

    // Assemble details section
    detailRow.appendChild(left);
    detailRow.appendChild(right);
    details.appendChild(detailRow);

    // Final assembly
    card.appendChild(topContainer);
    card.appendChild(details);

    return card;
}

function refreshModelDetail(modelId) {
    const details = document.getElementById("job-details_" + modelId);
    const left_detail = details.querySelector('.left-details');
    left_detail.innerHTML = '';

    const detailItems = [
        ['Epochs:', mapDataDetails[modelId].param.numEpoch],
        ['Learning Rate:', mapDataDetails[modelId].param.learningRate],
        ['Batch Size:', mapDataDetails[modelId].param.batchSize],
        ['Accuracy:', mapData.info[modelId].currentAccuracy],
        ['Loss:', mapData.info[modelId].currentLoss]
    ];
    detailItems.forEach(([label, value]) => {
        const p = document.createElement('p');
        const strong = document.createElement('strong');
        strong.textContent = label + ' ';
        p.appendChild(strong);
        p.appendChild(document.createTextNode(value));
        left_detail.appendChild(p);
    });

    const accCanvas = document.getElementById("accuracyChart_" + modelId);
    const lossCanvas = document.getElementById("lossChart_" + modelId);

    let logInterval = mapDataDetails[modelId].logInterval;
    let trainingInfo = mapDataDetails[modelId].trainingInfo;
    let validationInfo = mapDataDetails[modelId].validationInfo;
    let numBatchPerEpoch = mapData.info[modelId].numBatchPerEpoch;

    drawCharts(accCanvas, lossCanvas, modelId, getChartData(trainingInfo, validationInfo, logInterval, numBatchPerEpoch));
}

function getChartData(trainingInfo, validationInfo, logInterval, numBatchPerEpoch) {
    let data = {};
    trainingInfo = trainingInfo.sort((a, b) => {
        if (a.epochIdx < b.epochIdx) return -1;
        if (a.epochIdx > b.epochIdx) return 1;
        return a.batchIdx - b.batchIdx;
    })
    validationInfo = validationInfo.sort((a, b) => {
        return a.epochIdx - b.epochIdx;
    })
    let dataTrainingAcc = [];
    let dataTrainingLoss = [];
    let dataValidationAcc = [];
    let dataValidationLoss = [];
    for (let ele of trainingInfo) {
        dataTrainingAcc.push({
            x: (ele.epochIdx - 1) * numBatchPerEpoch + ele.batchIdx,
            y: ele.accuracy
        })
        dataTrainingLoss.push({
            x: (ele.epochIdx - 1) * numBatchPerEpoch + ele.batchIdx,
            y: ele.loss
        })
    }
    for (let ele of validationInfo) {
        dataValidationAcc.push({
            x: ele.epochIdx * numBatchPerEpoch,
            y: ele.accuracy
        })
        dataValidationLoss.push({
            x: ele.epochIdx * numBatchPerEpoch,
            y: ele.loss
        })
    }
    return {
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
}

function sendRequestModelDetail(modelId) {

}

function receiveWebsocketInfo() {

}

function formatNumber(n) {
    return n < 10 ? '0' + n : n;
}

function formatTimestamp(timestamp) {
    const date = new Date(timestamp);

    const dd = formatNumber(date.getDate());
    const mm = formatNumber(date.getMonth() + 1); // Months are 0-based
    const yyyy = date.getFullYear();

    const hh = formatNumber(date.getHours());
    const min = formatNumber(date.getMinutes());
    const ss = formatNumber(date.getSeconds());

    return dd + "/" + mm + "/" + yyyy + " " + hh + ":" + min + ":" + ss;
}

function getDurationFormatted(t1, t2) {
    let diff = Math.abs(t2 - t1); // difference in milliseconds

    const totalSeconds = Math.floor(diff / 1000);
    const days = Math.floor(totalSeconds / (24 * 60 * 60));
    const hours = Math.floor((totalSeconds % (24 * 60 * 60)) / 3600);
    const minutes = Math.floor((totalSeconds % 3600) / 60);
    const seconds = totalSeconds % 60;

    return days + "d" + hours + "h" + minutes + "m" + seconds + "s";
}