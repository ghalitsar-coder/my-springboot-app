// Users page functionality
class UsersPage {
    constructor() {
        this.users = [];
        this.filteredUsers = [];
        this.currentEditingId = null;
        this.init();
    }

    async init() {
        await this.loadUsers();
        this.setupEventListeners();
        this.setupModals();
        this.renderUsersTable();
        this.updateStats();
    }

    async loadUsers() {
        try {
            this.showLoading('usersTableBody');
            this.users = await orderingApp.fetchUsers();
            this.filteredUsers = [...this.users];
        } catch (error) {
            console.error('Error loading users:', error);
            document.getElementById('usersTableBody').innerHTML = 
                '<tr><td colspan="5" class="px-6 py-12 text-center text-red-600">Failed to load users</td></tr>';
        }
    }

    setupEventListeners() {
        // Search functionality
        const searchInput = document.getElementById('searchInput');
        if (searchInput) {
            searchInput.addEventListener('input', (e) => {
                this.filterUsers(e.target.value);
            });
        }

        // Refresh button
        const refreshBtn = document.getElementById('refreshBtn');
        if (refreshBtn) {
            refreshBtn.addEventListener('click', () => {
                this.loadUsers();
            });
        }
    }

    setupModals() {
        // User modal
        const addUserBtn = document.getElementById('addUserBtn');
        const userModal = document.getElementById('userModal');
        const closeModal = document.getElementById('closeModal');
        const cancelModal = document.getElementById('cancelModal');
        const userForm = document.getElementById('userForm');

        if (addUserBtn) {
            addUserBtn.addEventListener('click', () => {
                this.openUserModal();
            });
        }

        if (closeModal) {
            closeModal.addEventListener('click', () => {
                this.closeUserModal();
            });
        }

        if (cancelModal) {
            cancelModal.addEventListener('click', () => {
                this.closeUserModal();
            });
        }

        if (userForm) {
            userForm.addEventListener('submit', (e) => {
                e.preventDefault();
                this.saveUser();
            });
        }

        // User details modal
        const closeDetailsModal = document.getElementById('closeDetailsModal');
        if (closeDetailsModal) {
            closeDetailsModal.addEventListener('click', () => {
                this.closeUserDetailsModal();
            });
        }

        // Close modals when clicking outside
        window.addEventListener('click', (e) => {
            if (e.target === userModal) {
                this.closeUserModal();
            }
            if (e.target === document.getElementById('userDetailsModal')) {
                this.closeUserDetailsModal();
            }
        });
    }

    filterUsers(searchTerm = '') {
        this.filteredUsers = this.users.filter(user => {
            const searchLower = searchTerm.toLowerCase();
            return !searchTerm || 
                user.fullName?.toLowerCase().includes(searchLower) ||
                user.email?.toLowerCase().includes(searchLower) ||
                user.phone?.toLowerCase().includes(searchLower);
        });

        this.renderUsersTable();
        this.checkEmptyState();
    }

    renderUsersTable() {
        const tableBody = document.getElementById('usersTableBody');
        if (!tableBody) return;

        if (this.filteredUsers.length === 0) {
            this.checkEmptyState();
            return;
        }

        document.getElementById('emptyState')?.classList.add('hidden');

        tableBody.innerHTML = this.filteredUsers.map(user => `
            <tr class="table-row hover:bg-[#c08450]/5 transition-colors">
                <td class="px-6 py-4 whitespace-nowrap">
                    <div class="flex items-center">
                        ${getUserAvatar(user.fullName)}
                        <div class="ml-4">
                            <div class="text-sm font-medium text-gray-900">${user.fullName || 'Unknown'}</div>
                            <div class="text-sm text-gray-500">ID: ${user.id}</div>
                        </div>
                    </div>
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                    <div class="text-sm text-gray-900">${user.email || 'No email'}</div>
                    <div class="text-sm text-gray-500">${user.phone || 'No phone'}</div>
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                    <span class="inline-flex px-2 py-1 text-xs font-semibold rounded-full bg-green-100 text-green-800">
                        Active
                    </span>
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                    ${formatDate(user.createdAt)}
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                    <div class="flex space-x-2">
                        <button onclick="window.usersPage.viewUser(${user.id})" 
                                class="bg-blue-500 hover:bg-blue-600 text-white px-3 py-1 rounded-lg text-xs transition">
                            <i class="fas fa-eye"></i>
                        </button>
                        <button onclick="window.usersPage.editUser(${user.id})" 
                                class="bg-yellow-500 hover:bg-yellow-600 text-white px-3 py-1 rounded-lg text-xs transition">
                            <i class="fas fa-edit"></i>
                        </button>
                        <button onclick="window.usersPage.deleteUser(${user.id})" 
                                class="bg-red-500 hover:bg-red-600 text-white px-3 py-1 rounded-lg text-xs transition">
                            <i class="fas fa-trash"></i>
                        </button>
                    </div>
                </td>
            </tr>
        `).join('');
    }

    updateStats() {
        const totalUsers = this.users.length;
        const activeUsers = this.users.filter(u => u.active !== false).length;
        const today = new Date().toDateString();
        const newUsers = this.users.filter(u => 
            u.createdAt && new Date(u.createdAt).toDateString() === today
        ).length;

        document.getElementById('totalUsers').textContent = totalUsers;
        document.getElementById('activeUsers').textContent = activeUsers;
        document.getElementById('newUsers').textContent = newUsers;
    }

    checkEmptyState() {
        const emptyState = document.getElementById('emptyState');
        const tableBody = document.getElementById('usersTableBody');
        
        if (this.filteredUsers.length === 0) {
            emptyState?.classList.remove('hidden');
            if (tableBody) {
                tableBody.innerHTML = '';
            }
        } else {
            emptyState?.classList.add('hidden');
        }
    }

    openUserModal(user = null) {
        const modal = document.getElementById('userModal');
        const title = document.getElementById('modalTitle');
        
        this.currentEditingId = user ? user.id : null;
        title.textContent = user ? 'Edit User' : 'Add User';
        
        if (user) {
            document.getElementById('userName').value = user.fullName || '';
            document.getElementById('userEmail').value = user.email || '';
            document.getElementById('userPhone').value = user.phone || '';
            document.getElementById('userAddress').value = user.address || '';
        } else {
            document.getElementById('userForm').reset();
        }
        
        modal.classList.remove('hidden');
    }

    closeUserModal() {
        const modal = document.getElementById('userModal');
        modal.classList.add('hidden');
        this.currentEditingId = null;
    }

    async saveUser() {
        const fullName = document.getElementById('userName').value;
        const email = document.getElementById('userEmail').value;
        const phone = document.getElementById('userPhone').value;
        const address = document.getElementById('userAddress').value;

        const userData = {
            fullName,
            email,
            phone,
            address
        };

        try {
            if (this.currentEditingId) {
                await orderingApp.updateUser(this.currentEditingId, userData);
                orderingApp.showAlert('User updated successfully!', 'success');
            } else {
                await orderingApp.createUser(userData);
                orderingApp.showAlert('User added successfully!', 'success');
            }
            
            this.closeUserModal();
            await this.loadUsers();
            this.renderUsersTable();
            this.updateStats();
        } catch (error) {
            console.error('Error saving user:', error);
            orderingApp.showAlert('Error saving user. Please try again.', 'error');
        }
    }

    viewUser(id) {
        const user = this.users.find(u => u.id === id);
        if (!user) return;

        const modal = document.getElementById('userDetailsModal');
        const content = document.getElementById('userDetailsContent');
        
        content.innerHTML = `
            <div class="space-y-6">
                <div class="flex items-center">
                    ${getUserAvatar(user.fullName)}
                    <div class="ml-4">
                        <h4 class="text-xl font-bold text-gray-900">${user.fullName || 'Unknown User'}</h4>
                        <p class="text-gray-600">User ID: ${user.id}</p>
                    </div>
                </div>
                
                <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                    <div>
                        <h5 class="font-medium text-gray-700 mb-2">Contact Information</h5>
                        <div class="space-y-2 text-sm">
                            <div class="flex items-center">
                                <i class="fas fa-envelope text-gray-400 w-4"></i>
                                <span class="ml-2">${user.email || 'No email'}</span>
                            </div>
                            <div class="flex items-center">
                                <i class="fas fa-phone text-gray-400 w-4"></i>
                                <span class="ml-2">${user.phone || 'No phone'}</span>
                            </div>
                            <div class="flex items-center">
                                <i class="fas fa-map-marker-alt text-gray-400 w-4"></i>
                                <span class="ml-2">${user.address || 'No address'}</span>
                            </div>
                        </div>
                    </div>
                    
                    <div>
                        <h5 class="font-medium text-gray-700 mb-2">Account Details</h5>
                        <div class="space-y-2 text-sm">
                            <div class="flex justify-between">
                                <span>Status:</span>
                                <span class="inline-flex px-2 py-1 text-xs font-semibold rounded-full bg-green-100 text-green-800">
                                    Active
                                </span>
                            </div>
                            <div class="flex justify-between">
                                <span>Joined:</span>
                                <span>${formatDate(user.createdAt)}</span>
                            </div>
                            <div class="flex justify-between">
                                <span>Last Updated:</span>
                                <span>${formatDate(user.updatedAt)}</span>
                            </div>
                        </div>
                    </div>
                </div>
                
                <div class="flex gap-4 pt-4">
                    <button onclick="window.usersPage.editUser(${user.id}); window.usersPage.closeUserDetailsModal();" 
                            class="flex-1 bg-[#c08450] hover:bg-[#9a6c3e] text-white py-2 rounded-lg font-medium transition">
                        <i class="fas fa-edit mr-2"></i>Edit User
                    </button>
                    <button onclick="window.usersPage.deleteUser(${user.id}); window.usersPage.closeUserDetailsModal();" 
                            class="flex-1 bg-red-500 hover:bg-red-600 text-white py-2 rounded-lg font-medium transition">
                        <i class="fas fa-trash mr-2"></i>Delete User
                    </button>
                </div>
            </div>
        `;
        
        modal.classList.remove('hidden');
    }

    closeUserDetailsModal() {
        const modal = document.getElementById('userDetailsModal');
        modal.classList.add('hidden');
    }

    editUser(id) {
        const user = this.users.find(u => u.id === id);
        if (user) {
            this.openUserModal(user);
        }
    }

    async deleteUser(id) {
        if (!confirm('Are you sure you want to delete this user?')) {
            return;
        }

        try {
            await orderingApp.deleteUser(id);
            orderingApp.showAlert('User deleted successfully!', 'success');
            await this.loadUsers();
            this.renderUsersTable();
            this.updateStats();
        } catch (error) {
            console.error('Error deleting user:', error);
            orderingApp.showAlert('Error deleting user. Please try again.', 'error');
        }
    }

    showLoading(containerId) {
        const container = document.getElementById(containerId);
        if (container) {
            container.innerHTML = `
                <tr>
                    <td colspan="5" class="px-6 py-12 text-center">
                        <div class="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-[#c08450] mb-4"></div>
                        <p class="text-gray-600">Loading users...</p>
                    </td>
                </tr>
            `;
        }
    }
}

// Initialize users page when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    if (document.getElementById('usersTableContainer')) {
        window.usersPage = new UsersPage();
        
        // Setup edit user form if it exists
        const editUserForm = document.getElementById('editUserForm');
        if (editUserForm) {
            editUserForm.addEventListener('submit', async (e) => {
                e.preventDefault();
                await usersPage.handleEditUser(e.target);
            });
        }
    }
});
