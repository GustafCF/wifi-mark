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
                $('#token').text(response.acessToken);
                $('#expiresIn').text(response.expiresIn);
                $('#response').show();
            },
            error: function(xhr) {
                alert('Erro ao fazer login: ' + xhr.responseText);
            }
        });
    });
});