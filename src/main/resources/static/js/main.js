// Main JavaScript file for Ordering System
class OrderingSystem {
    constructor() {
        this.apiUrl = '/api';
        this.cart = [];
        this.init();
    }

    init() {
        this.setupEventListeners();
        this.addAnimations();
    }

    setupEventListeners() {
        // Add smooth scrolling for navigation links
        document.querySelectorAll('a[href^="#"]').forEach(anchor => {
            anchor.addEventListener('click', function (e) {
                e.preventDefault();
                document.querySelector(this.getAttribute('href')).scrollIntoView({
                    behavior: 'smooth'
                });
            });
        });

        // Add hover effects to cards
        document.querySelectorAll('.card').forEach(card => {
            card.addEventListener('mouseenter', function() {
                this.style.transform = 'translateY(-5px)';
            });
            
            card.addEventListener('mouseleave', function() {
                this.style.transform = 'translateY(0)';
            });
        });
    }

    addAnimations() {
        // Add fade-in animation to elements as they come into view
        const observerOptions = {
            threshold: 0.1,
            rootMargin: '0px 0px -50px 0px'
        };

        const observer = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    entry.target.classList.add('fade-in');
                }
            });
        }, observerOptions);

        // Observe all cards and sections
        document.querySelectorAll('.card, .hero-section, .section-title').forEach(el => {
            observer.observe(el);
        });
    }

    // API Methods
    async fetchUsers() {
        try {
            const response = await fetch(`${this.apiUrl}/users`);
            if (!response.ok) throw new Error('Failed to fetch users');
            return await response.json();
        } catch (error) {
            console.error('Error fetching users:', error);
            this.showAlert('Error loading users', 'danger');
            return [];
        }
    }

    async fetchProducts() {
        try {
            const response = await fetch(`${this.apiUrl}/products`);
            if (!response.ok) throw new Error('Failed to fetch products');
            return await response.json();
        } catch (error) {
            console.error('Error fetching products:', error);
            this.showAlert('Error loading products', 'danger');
            return [];
        }
    }

    async createUser(userData) {
        try {
            const response = await fetch(`${this.apiUrl}/users`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(userData)
            });
            if (!response.ok) throw new Error('Failed to create user');
            return await response.json();
        } catch (error) {
            console.error('Error creating user:', error);
            this.showAlert('Error creating user', 'danger');
            throw error;
        }
    }

    // UI Helper Methods
    showAlert(message, type = 'info') {
        const alertContainer = document.getElementById('alertContainer') || this.createAlertContainer();
        const alert = document.createElement('div');
        alert.className = `alert alert-${type} alert-dismissible fade show alert-custom`;
        alert.innerHTML = `
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        `;
        alertContainer.appendChild(alert);

        // Auto-dismiss after 5 seconds
        setTimeout(() => {
            if (alert.parentNode) {
                alert.remove();
            }
        }, 5000);
    }

    createAlertContainer() {
        const container = document.createElement('div');
        container.id = 'alertContainer';
        container.className = 'position-fixed top-0 end-0 p-3';
        container.style.zIndex = '9999';
        document.body.appendChild(container);
        return container;
    }

    showLoading(element) {
        element.innerHTML = '<div class="loading">Loading...</div>';
    }

    formatPrice(price) {
        return new Intl.NumberFormat('id-ID', {
            style: 'currency',
            currency: 'IDR'
        }).format(price);
    }

    formatDate(dateString) {
        return new Date(dateString).toLocaleDateString('id-ID', {
            year: 'numeric',
            month: 'long',
            day: 'numeric'
        });
    }

    // Cart functionality
    addToCart(product) {
        const existingItem = this.cart.find(item => item.id === product.id);
        if (existingItem) {
            existingItem.quantity += 1;
        } else {
            this.cart.push({ ...product, quantity: 1 });
        }
        this.updateCartUI();
        this.showAlert(`${product.name} added to cart`, 'success');
    }

    removeFromCart(productId) {
        this.cart = this.cart.filter(item => item.id !== productId);
        this.updateCartUI();
        this.showAlert('Item removed from cart', 'info');
    }

    updateCartUI() {
        const cartCount = document.getElementById('cartCount');
        const cartTotal = document.getElementById('cartTotal');
        
        if (cartCount) {
            cartCount.textContent = this.cart.reduce((sum, item) => sum + item.quantity, 0);
        }
        
        if (cartTotal) {
            const total = this.cart.reduce((sum, item) => sum + (item.price * item.quantity), 0);
            cartTotal.textContent = this.formatPrice(total);
        }
    }

    // Form validation
    validateForm(formElement) {
        let isValid = true;
        const requiredFields = formElement.querySelectorAll('[required]');
        
        requiredFields.forEach(field => {
            if (!field.value.trim()) {
                field.classList.add('is-invalid');
                isValid = false;
            } else {
                field.classList.remove('is-invalid');
            }
        });
        
        return isValid;
    }

    // Table rendering helpers
    renderTable(containerId, data, columns, actions = []) {
        const container = document.getElementById(containerId);
        if (!container) return;

        if (data.length === 0) {
            container.innerHTML = '<div class="text-center p-4">No data available</div>';
            return;
        }

        let tableHTML = `
            <div class="table-responsive">
                <table class="table table-striped table-hover">
                    <thead class="table-dark">
                        <tr>
                            ${columns.map(col => `<th>${col.title}</th>`).join('')}
                            ${actions.length > 0 ? '<th>Actions</th>' : ''}
                        </tr>
                    </thead>
                    <tbody>
        `;

        data.forEach(row => {
            tableHTML += '<tr>';
            columns.forEach(col => {
                let value = row[col.field];
                if (col.formatter) {
                    value = col.formatter(value, row);
                }
                tableHTML += `<td>${value}</td>`;
            });
            
            if (actions.length > 0) {
                tableHTML += '<td>';
                actions.forEach(action => {
                    tableHTML += `<button class="btn btn-sm btn-${action.class} me-1" onclick="${action.handler}(${row.id})">${action.title}</button>`;
                });
                tableHTML += '</td>';
            }
            tableHTML += '</tr>';
        });

        tableHTML += `
                    </tbody>
                </table>
            </div>
        `;

        container.innerHTML = tableHTML;
    }
}

// Initialize the application
const orderingApp = new OrderingSystem();

// Utility functions
function showModal(modalId) {
    const modal = new bootstrap.Modal(document.getElementById(modalId));
    modal.show();
}

function hideModal(modalId) {
    const modal = bootstrap.Modal.getInstance(document.getElementById(modalId));
    if (modal) modal.hide();
}

// Export for use in other files
window.OrderingSystem = OrderingSystem;
window.orderingApp = orderingApp;
