$(document).ready(function() {
    $('#loginForm').off('submit').submit(function(event) {
        event.preventDefault();

        const loginData = {
            username: $('#username').val(),
            password: $('#password').val()
        };

        $.ajax({
            url: '/auth/login',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(loginData),
            success: function(response) {
                $('#response').show();
                if(loginData.username === 'admin') {
                    localStorage.setItem('token', response.acessToken);
                    localStorage.setItem('username', loginData.username);
                    window.location.href = '/index';
                }
            },
            error: function(xhr) {
                alert('Erro ao fazer login: ' + xhr.responseText);
            }
        });
    });
});