/** ________________ DATA ________________ */
let currentPage = 1;
let currentSortField = null;
let currentIsAscending = null;
let mapModelChart = {};
let mapData = {};
let mapDataDetails = {};
let stompClient = null;

/** ________________ CHANGE PAGE ________________ */
function viewFinishedJob() {
    window.location.href = '/home/done';
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

function sortBy(field, name) {
    const dropdown = document.getElementById('sortDropdown');
    dropdown.classList.add('d-none');
    if (currentSortField == field) return;
    currentSortField = field;
    const text = document.getElementById('sortToggleButton');
    text.textContent = name;
    sendRequestData();
}

function setIsIncrease(isAscending) {
    const dropdown = document.getElementById('orderDropdown');
    dropdown.classList.add('d-none');
    if (currentIsAscending == isAscending) return;
    currentIsAscending = isAscending;
    const text = document.getElementById('orderToggleButton');
    text.textContent = isAscending ? "Increase" : "Decrease";
    sendRequestData();
}

/** ________________ SHOW MODEL DETAIL ________________ */
function toggleDetails(button) {
    const jobCard = button.closest('.job-card');
    const detailsDiv = jobCard.querySelector('.job-details');
    const modelId = jobCard.getAttribute("modelId");

    if (detailsDiv.classList.contains('d-none')) {
        detailsDiv.classList.remove('d-none');
        button.textContent = '▲';
        if (mapDataDetails[modelId] != null) {
            refreshModelDetail(modelId);
        }
        sendRequestModelDetail(modelId);
    } else {
        detailsDiv.classList.add('d-none');
        button.textContent = '▼';
    }
}

/** ________________ PAGINATE ________________ */
function changePage(page) {
    if (currentPage == page) return;
    currentPage = page;
    sendRequestData();
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

    if (totalPages == null) totalPages = 1;

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
                        ticks: { color: '#ccc' },
                        type: 'linear'
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
                        ticks: { color: '#ccc' },
                        type: 'linear'
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

    sendRequestJob()
    registerWebsocketInfo();
});

async function submitNewJob(event) {
    event.preventDefault();

    const epoch = document.querySelector('input[name="epoch"]').value;
    const batchSize = document.querySelector('input[name="batchSize"]').value;
    const learningRate = document.querySelector('input[name="learningRate"]').value;
    const logInterval = document.querySelector('input[name="logInterval"]').value;

    const data = {
        "epochs": epoch,
        "batchSize": batchSize,
        "learningRate": learningRate,
        "logIntervals": logInterval
    }

    try {
        fetch('/api/admin/model/new_job', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        })
        .then(async response => {
            if (response.status != 200) {
                let message = await response.text();
                console.log(message);
            } else {
                const result = await response.json();
                console.log(result);
                alert("Send create job successfully. Please wait!");
                hidePopup();
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

async function sendRequestJob() {
    try {
        fetch('/api/view/model/training/all', {
            method: 'GET',
            headers: { 'Content-Type': 'application/json' },
        })
        .then(async response => {
            if (response.status != 200) {
                let message = await response.text();
                console.error("Error:", message);
            }
            else {
                const result = await response.json();
                console.log(result);
                saveData(result);
                refreshPage();
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
    let pageIdx = currentPage - 1;
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
                    refreshPage();
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
    mapModelChart = {};

    mapData = {};
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
    let pageIdx = mapData.pageIdx;
    let totalPage = mapData.numPage;
    currentPage = pageIdx + 1;
    renderPagination(totalPage, currentPage);

    let jobContainer = document.getElementById("job_card_container");
    jobContainer.innerHTML = '';
    let listModelId = Object.keys(mapData.info);
    for (let modelId of listModelId) {
        let card = createJobCard(modelId);
        jobContainer.appendChild(card);
    }
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

    // Progress bar
    const progressContainer = document.createElement('div');
    progressContainer.className = 'progress';
    const progressBar = document.createElement('div');
    progressBar.id = 'progress-bar_' + modelId;
    progressBar.className = 'progress-bar';
    progressBar.style.width = mapData.info[modelId].progress + '%';
    progressBar.textContent = mapData.info[modelId].progress + '%';
    progressContainer.appendChild(progressBar);

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
    topContainer.appendChild(progressContainer);
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

    const detailItems = [
        ['Epochs:', ''],
        ['Learning Rate:', ''],
        ['Batch Size:', ''],
        ['Accuracy:', ''],
        ['Loss:', '']
    ];
    detailItems.forEach(([label, value]) => {
        const p = document.createElement('p');
        const strong = document.createElement('strong');
        strong.textContent = label + ' ';
        p.appendChild(strong);
        p.appendChild(document.createTextNode(value));
        left.appendChild(p);
    });

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
    if (mapDataDetails[modelId] == null) return;
    const details = document.getElementById("job-details_" + modelId);
    if (!details) return;
    const left_detail = details.querySelector('.left-details');
    left_detail.innerHTML = '';

    const detailItems = [
        ['Epochs:', mapDataDetails[modelId].param.numEpoch],
        ['Learning Rate:', mapDataDetails[modelId].param.learningRate],
        ['Batch Size:', mapDataDetails[modelId].param.batchSize],
        ['Accuracy:', Math.round(mapData.info[modelId].currentAccuracy * 10000) / 100 + "%"],
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
            datasets: [
                {
                    label: 'Training Accuracy',
                    data: dataTrainingAcc,
                    borderColor: 'orange',
                    fill: false,
                    tension: 0.3
                },
                {
                    label: 'Validation Accuracy',
                    data: dataValidationAcc,
                    borderColor: 'white',
                    fill: false,
                    tension: 0.3
                }
            ]
        },
        loss: {
            datasets: [
                {
                    label: 'Training Loss',
                    data: dataTrainingLoss,
                    borderColor: 'orange',
                    fill: false,
                    tension: 0.3
                },
                {
                    label: 'Validation Loss',
                    data: dataValidationLoss,
                    borderColor: 'white',
                    fill: false,
                    tension: 0.3
                }
            ]
        }
    };
}

async function sendRequestModelDetail(modelId) {
    try {
        fetch('/api/view/model/training/' + modelId, {
            method: 'GET',
            headers: { 'Content-Type': 'application/json' },
        })
            .then(async response => {
                if (response.status != 200) {
                    let message = await response.text();
                    console.error("Error:", message);
                    alert("Something went wrong");
                }
                else {
                    const result = await response.json();
                    console.log(result);
                    saveDataDetail(result);
                    refreshModelDetail(modelId);
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

function saveDataDetail(data) {
    mapDataDetails[data.modelId] = data;
}

function registerWebsocketInfo() {
    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, (frame) => {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/model_init_data', (message) => {
            const msg = JSON.parse(message.body);
            receiveModelInit(msg);
        });

        stompClient.subscribe('/topic/model_training_data', (message) => {
            const msg = JSON.parse(message.body);
            receiveModelTrain(msg);
        });

        stompClient.subscribe('/topic/model_validation_data', (message) => {
            const msg = JSON.parse(message.body);
            receiveModelValidation(msg);
        });

        stompClient.subscribe('/topic/model_end_data', (message) => {
            const msg = JSON.parse(message.body);
            receiveModelEnd(msg);
        });
    });
}

function receiveModelInit(data) {
    saveData(data);
    refreshPage();
}

function receiveModelTrain(data) {
    if (mapData.info[data.model_id]) {
        mapData.info[data.model_id].currentEpochIdx = Math.max(mapData.info[data.model_id].currentEpochIdx, data.epoch_idx);
        mapData.info[data.model_id].currentBatchIdx = Math.max(mapData.info[data.model_id].currentBatchIdx, data.batch_idx);
        mapData.info[data.model_id].currentAccuracy = data.train_acc;
        mapData.info[data.model_id].currentLoss = data.train_loss;

        const spans = [
            ['acc', 'Accuracy: ' + Math.round(mapData.info[data.model_id].currentAccuracy * 10000) / 100 + "%"],
            ['loss', 'Loss: ' + mapData.info[data.model_id].currentLoss],
            ['epoch', 'Epoch: ' + mapData.info[data.model_id].currentEpochIdx + "/" + mapData.info[data.model_id].totalEpoch],
            ['batch', 'Batch: ' + mapData.info[data.model_id].currentBatchIdx + "/" + mapData.info[data.model_id].numBatchPerEpoch],
        ];
        spans.forEach(arr => {
            const ele = document.getElementById('job-description_' + arr[0] + "_" + data.model_id);
            if (ele) {
                ele.textContent = arr[1];
            }
        })

         // Progress bar
        const progressBar = document.getElementById('progress-bar_' + data.model_id);
        if (progressBar) {
            let totalBatch = mapData.info[data.model_id].totalEpoch * mapData.info[data.model_id].numBatchPerEpoch;
            let numBatchDone = Math.max(mapData.info[data.model_id].currentEpochIdx - 1, 0) * mapData.info[data.model_id].numBatchPerEpoch + mapData.info[data.model_id].currentBatchIdx;
            let progress = Math.round(numBatchDone / totalBatch * 10000) / 100;
            progressBar.style.width = progress + '%';
            progressBar.textContent = progress + '%';
        }
    }
    if (mapDataDetails[data.model_id]) {
        mapDataDetails[data.model_id].trainingInfo.push({
            epochIdx: data.epoch_idx,
            batchIdx: data.batch_idx,
            accuracy: data.train_acc,
            loss: data.train_loss
        })
        const detailEle = document.getElementById("job-details_" + data.model_id);
        if (detailEle && !detailEle.classList.contains('d-none')) {
            refreshModelDetail(data.model_id);
        }
    }
}

function receiveModelValidation(data) {
    if (mapDataDetails[data.model_id]) {
        mapDataDetails[data.model_id].validationInfo.push({
            epochIdx: data.epoch_idx,
            accuracy: data.val_acc,
            loss: data.val_loss
        })
        const detailEle = document.getElementById("job-details_" + data.model_id);
        if (detailEle && !detailEle.classList.contains('d-none')) {
            refreshModelDetail(data.model_id);
        }
    }
}

function receiveModelEnd(data) {
    const ele = document.getElementById('job-card_' + data.model_id);
    if (ele) {
        ele.remove();
    }
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