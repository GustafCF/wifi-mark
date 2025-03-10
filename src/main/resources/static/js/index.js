$(document).ready(function() {
    const token = localStorage.getItem('token');
    const username = localStorage.getItem('username');

    if (!token || !username) {
        alert('Usuário não autenticado. Redirecionando para a página de login...');
        window.location.href = '/';
        return;
    }
    
    $('#userInfo').text(`Perfil logado: ${username}!`);

    function loadUsers() {
        $.ajax({
            url: '/us/list',
            type: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`
            },
            success: function(response) {
                const usersTable = $('#usersTable tbody');
                usersTable.empty();

                response.forEach(user => {
                    const row = `
                        <tr>
                            <td>${user.id}</td>
                            <td>${user.username}</td>
                            <td>
                                <button class="btn btn-danger btn-sm delete-btn" data-id="${user.id}">Excluir</button>
                            </td>
                        </tr>
                    `;
                    usersTable.append(row);
                });

                $('.delete-btn').click(function() {
                    const userId = $(this).data('id');
                    deleteUser(userId);
                });

                $('#usersTableContainer').show();
            },
            error: function(xhr) {
                console.error('Erro ao carregar usuários:', xhr.responseText);
                alert('Erro ao carregar usuários. Verifique o console para mais detalhes.');
            }
        });
    }

    function deleteUser(userId) {
        if (confirm('Tem certeza que deseja excluir este usuário?')) {
            $.ajax({
                url: `/us/delete/${userId}`,
                type: 'DELETE',
                headers: {
                    'Authorization': `Bearer ${token}`
                },
                success: function() {
                    alert('Usuário excluído com sucesso!');
                    loadUsers();
                },
                error: function(xhr) {
                    console.error('Erro ao excluir usuário:', xhr.responseText);
                    alert('Erro ao excluir usuário. Verifique o console para mais detalhes.');
                }
            });
        }
    }

    $('#listUsersButton').click(function() {
        loadUsers(); 
    });

    $('#logoutButton').click(function() {
        localStorage.removeItem('token');
        localStorage.removeItem('username');
        window.location.href = '/';
    });
});