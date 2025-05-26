async function sendLogin(event) {
    event.preventDefault();

    const username = document.querySelector('input[name="username"]').value;
    const password = document.querySelector('input[name="password"]').value;
    const isRememberMe = document.querySelector('input[name="remember-me"]').checked;

    const data = {
        username: username,
        password: password,
        isRememberMe: isRememberMe
    }

    try {
        fetch('/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        })
        .then(async response => {
            if (response.status != 200) {
                const message = await response.text();
                const errorMsg = document.getElementById('errorMessage');
                errorMsg.textContent = message;
                errorMsg.classList.remove('d-none');
                errorMsg.classList.add('animate__shakeX');
                setTimeout(() => errorMsg.classList.remove('animate__shakeX'), 2000);
            } else {
                const result = await response.json();
                const url = result.params.redirectUrl;
                window.location.href = url
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

    return true;
}
