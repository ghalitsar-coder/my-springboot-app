// Products page functionality
class ProductsPage {
    constructor() {
        this.products = [];
        this.categories = [];
        this.filteredProducts = [];
        this.currentCategory = '';
        this.currentEditingId = null;
        this.init();
    }

    async init() {
        await this.loadProducts();
        await this.loadCategories();
        this.setupFilters();
        this.setupModals();
        this.renderProducts();
        this.renderCategories();
    }

    async loadProducts() {
        try {
            this.showLoading('productsContainer');
            this.products = await orderingApp.fetchProducts();
            this.filteredProducts = [...this.products];
        } catch (error) {
            console.error('Error loading products:', error);
            document.getElementById('productsContainer').innerHTML = 
                '<div class="text-center p-4"><div class="alert alert-danger">Failed to load products</div></div>';
        }
    }

    async loadCategories() {
        try {
            // For now, extract categories from products
            const categorySet = new Set(this.products.map(p => p.category?.name).filter(Boolean));
            this.categories = Array.from(categorySet).map(name => ({ name }));
        } catch (error) {
            console.error('Error loading categories:', error);
        }
    }

    setupFilters() {
        // Search functionality
        const searchInput = document.getElementById('searchInput');
        if (searchInput) {
            searchInput.addEventListener('input', (e) => {
                this.filterProducts(e.target.value, this.currentCategory);
            });
        }

        // Sort functionality
        const sortSelect = document.getElementById('sortSelect');
        if (sortSelect) {
            sortSelect.addEventListener('change', (e) => {
                this.sortProducts(e.target.value);
            });
        }

        // Category filters
        document.addEventListener('click', (e) => {
            if (e.target.classList.contains('category-btn')) {
                e.preventDefault();
                this.selectCategory(e.target.dataset.category);
            }
        });
    }

    setupModals() {
        // Product modal
        const addProductBtn = document.getElementById('addProductBtn');
        const productModal = document.getElementById('productModal');
        const closeModal = document.getElementById('closeModal');
        const cancelModal = document.getElementById('cancelModal');
        const productForm = document.getElementById('productForm');

        if (addProductBtn) {
            addProductBtn.addEventListener('click', () => {
                this.openProductModal();
            });
        }

        if (closeModal) {
            closeModal.addEventListener('click', () => {
                this.closeProductModal();
            });
        }

        if (cancelModal) {
            cancelModal.addEventListener('click', () => {
                this.closeProductModal();
            });
        }

        if (productForm) {
            productForm.addEventListener('submit', (e) => {
                e.preventDefault();
                this.saveProduct();
            });
        }

        // Cart modal
        const cartBtn = document.getElementById('cartBtn');
        const cartBtnMobile = document.getElementById('cartBtnMobile');
        const cartModal = document.getElementById('cartModal');
        const closeCartModal = document.getElementById('closeCartModal');
        const clearCartBtn = document.getElementById('clearCartBtn');
        const checkoutBtn = document.getElementById('checkoutBtn');

        if (cartBtn) {
            cartBtn.addEventListener('click', () => {
                this.showCartModal();
            });
        }

        if (cartBtnMobile) {
            cartBtnMobile.addEventListener('click', () => {
                this.showCartModal();
            });
        }

        if (closeCartModal) {
            closeCartModal.addEventListener('click', () => {
                this.closeCartModal();
            });
        }

        if (clearCartBtn) {
            clearCartBtn.addEventListener('click', () => {
                orderingApp.clearCart();
                this.showCartModal(); // Refresh cart display
            });
        }

        if (checkoutBtn) {
            checkoutBtn.addEventListener('click', () => {
                this.checkout();
            });
        }

        // Close modals when clicking outside
        window.addEventListener('click', (e) => {
            if (e.target === productModal) {
                this.closeProductModal();
            }
            if (e.target === cartModal) {
                this.closeCartModal();
            }
        });
    }

    selectCategory(category) {
        this.currentCategory = category;
        
        // Update category buttons
        const categoryBtns = document.querySelectorAll('.category-btn');
        categoryBtns.forEach(btn => {
            btn.classList.remove('active');
            if (btn.dataset.category === category) {
                btn.classList.add('active');
            }
        });

        this.filterProducts(document.getElementById('searchInput')?.value || '', category);
    }

    filterProducts(searchTerm = '', category = '') {
        this.filteredProducts = this.products.filter(product => {
            const matchesSearch = !searchTerm || 
                product.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
                product.description?.toLowerCase().includes(searchTerm.toLowerCase());
            
            const matchesCategory = !category || 
                product.category?.name === category;

            return matchesSearch && matchesCategory;
        });

        this.renderProducts();
        this.updateProductCount();
    }

    sortProducts(sortBy) {
        switch (sortBy) {
            case 'name':
                this.filteredProducts.sort((a, b) => a.name.localeCompare(b.name));
                break;
            case 'price':
                this.filteredProducts.sort((a, b) => (a.price || 0) - (b.price || 0));
                break;
            case 'price-desc':
                this.filteredProducts.sort((a, b) => (b.price || 0) - (a.price || 0));
                break;
            case 'newest':
                this.filteredProducts.sort((a, b) => (b.id || 0) - (a.id || 0));
                break;
        }
        this.renderProducts();
    }

    renderCategories() {
        const categoryFilters = document.getElementById('categoryFilters');
        if (!categoryFilters) return;

        // Add categories to filter buttons
        this.categories.forEach(category => {
            const button = document.createElement('button');
            button.className = 'category-btn px-6 py-2 rounded-full border border-gray-300 text-sm font-medium hover:border-[#c08450] transition';
            button.dataset.category = category.name;
            button.textContent = category.name;
            categoryFilters.appendChild(button);
        });

        // Add categories to product form
        const productCategory = document.getElementById('productCategory');
        if (productCategory) {
            this.categories.forEach(category => {
                const option = document.createElement('option');
                option.value = category.name;
                option.textContent = category.name;
                productCategory.appendChild(option);
            });
        }
    }

    renderProducts() {
        const container = document.getElementById('productsContainer');
        if (!container) return;

        if (this.filteredProducts.length === 0) {
            document.getElementById('emptyState')?.classList.remove('hidden');
            container.innerHTML = '';
            return;
        }

        document.getElementById('emptyState')?.classList.add('hidden');

        container.innerHTML = this.filteredProducts.map(product => `
            <div class="product-card bg-white rounded-2xl shadow-lg overflow-hidden group">
                <div class="relative overflow-hidden">
                    <img src="${product.image || 'https://images.unsplash.com/photo-1509042239860-f550ce710b93?ixlib=rb-1.2.1&auto=format&fit=crop&w=800&q=80'}" 
                         alt="${product.name}" 
                         class="w-full h-64 object-cover group-hover:scale-105 transition-transform duration-300"
                         onerror="this.src='https://images.unsplash.com/photo-1509042239860-f550ce710b93?ixlib=rb-1.2.1&auto=format&fit=crop&w=800&q=80'">
                    <div class="absolute top-4 right-4 bg-white/90 backdrop-blur-sm px-3 py-1 rounded-full">
                        <span class="text-sm font-bold text-[#c08450]">${orderingApp.formatPrice(product.price || 0)}</span>
                    </div>
                    ${product.available === false ? `
                        <div class="absolute inset-0 bg-black/50 flex items-center justify-center">
                            <span class="bg-red-500 text-white px-4 py-2 rounded-full text-sm font-medium">Out of Stock</span>
                        </div>
                    ` : ''}
                </div>
                <div class="p-6">
                    <div class="flex justify-between items-start mb-2">
                        <h3 class="text-xl font-bold display-font">${product.name}</h3>
                        ${product.category ? `
                            <span class="bg-[#c08450]/10 text-[#c08450] px-2 py-1 rounded-full text-xs font-medium">
                                ${product.category.name}
                            </span>
                        ` : ''}
                    </div>
                    <p class="text-gray-600 mb-4 line-clamp-2">${product.description || 'No description available'}</p>
                    <div class="flex justify-between items-center mb-4">
                        <div class="flex items-center">
                            ${[1,2,3,4,5].map(star => `
                                <i class="fas fa-star text-yellow-400 text-sm"></i>
                            `).join('')}
                            <span class="text-sm text-gray-600 ml-2">(4.8)</span>
                        </div>
                    </div>
                    <div class="flex gap-2">
                        <button onclick="orderingApp.addToCart(${product.id})" 
                                class="flex-1 bg-[#c08450] hover:bg-[#9a6c3e] text-white px-4 py-2 rounded-lg text-sm font-medium transition ${product.available === false ? 'opacity-50 cursor-not-allowed' : ''}"
                                ${product.available === false ? 'disabled' : ''}>
                            <i class="fas fa-shopping-cart mr-1"></i> Add to Cart
                        </button>
                        <button onclick="window.productsPage.editProduct(${product.id})" 
                                class="bg-blue-500 hover:bg-blue-600 text-white px-3 py-2 rounded-lg text-sm transition">
                            <i class="fas fa-edit"></i>
                        </button>
                        <button onclick="window.productsPage.deleteProduct(${product.id})" 
                                class="bg-red-500 hover:bg-red-600 text-white px-3 py-2 rounded-lg text-sm transition">
                            <i class="fas fa-trash"></i>
                        </button>
                    </div>
                </div>
            </div>
        `).join('');
    }

    updateProductCount() {
        const productCount = document.getElementById('productCount');
        if (productCount) {
            const total = this.filteredProducts.length;
            const totalProducts = this.products.length;
            if (total === totalProducts) {
                productCount.textContent = `${total} products`;
            } else {
                productCount.textContent = `${total} of ${totalProducts} products`;
            }
        }
    }

    openProductModal(product = null) {
        const modal = document.getElementById('productModal');
        const title = document.getElementById('modalTitle');
        
        this.currentEditingId = product ? product.id : null;
        title.textContent = product ? 'Edit Product' : 'Add Product';
        
        if (product) {
            document.getElementById('productName').value = product.name || '';
            document.getElementById('productDescription').value = product.description || '';
            document.getElementById('productPrice').value = product.price || '';
            document.getElementById('productCategory').value = product.category?.name || '';
            document.getElementById('productImage').value = product.image || '';
            document.getElementById('productAvailable').checked = product.available !== false;
        } else {
            document.getElementById('productForm').reset();
        }
        
        modal.classList.remove('hidden');
    }

    closeProductModal() {
        const modal = document.getElementById('productModal');
        modal.classList.add('hidden');
        this.currentEditingId = null;
    }

    async saveProduct() {
        const name = document.getElementById('productName').value;
        const description = document.getElementById('productDescription').value;
        const price = parseFloat(document.getElementById('productPrice').value);
        const categoryName = document.getElementById('productCategory').value;
        const image = document.getElementById('productImage').value;
        const available = document.getElementById('productAvailable').checked;

        const productData = {
            name,
            description,
            price,
            image,
            available,
            category: categoryName ? { name: categoryName } : null
        };

        try {
            if (this.currentEditingId) {
                await orderingApp.updateProduct(this.currentEditingId, productData);
                orderingApp.showAlert('Product updated successfully!', 'success');
            } else {
                await orderingApp.createProduct(productData);
                orderingApp.showAlert('Product added successfully!', 'success');
            }
            
            this.closeProductModal();
            await this.loadProducts();
            this.renderProducts();
            this.updateProductCount();
        } catch (error) {
            console.error('Error saving product:', error);
            orderingApp.showAlert('Error saving product. Please try again.', 'error');
        }
    }

    editProduct(id) {
        const product = this.products.find(p => p.id === id);
        if (product) {
            this.openProductModal(product);
        }
    }

    async deleteProduct(id) {
        if (!confirm('Are you sure you want to delete this product?')) {
            return;
        }

        try {
            await orderingApp.deleteProduct(id);
            orderingApp.showAlert('Product deleted successfully!', 'success');
            await this.loadProducts();
            this.renderProducts();
            this.updateProductCount();
        } catch (error) {
            console.error('Error deleting product:', error);
            orderingApp.showAlert('Error deleting product. Please try again.', 'error');
        }
    }

    showCartModal() {
        const modal = document.getElementById('cartModal');
        const cartItems = document.getElementById('cartItems');
        const cartTotal = document.getElementById('cartTotal');
        
        const cart = orderingApp.getCart();
        
        if (cart.length === 0) {
            cartItems.innerHTML = `
                <div class="text-center py-12">
                    <i class="fas fa-shopping-cart text-6xl text-gray-300 mb-4"></i>
                    <h3 class="text-xl font-bold text-gray-600 mb-2">Your cart is empty</h3>
                    <p class="text-gray-500">Add some delicious items to get started!</p>
                </div>
            `;
            cartTotal.textContent = '$0.00';
        } else {
            cartItems.innerHTML = cart.map(item => {
                const product = this.products.find(p => p.id === item.id);
                return `
                    <div class="flex items-center justify-between py-4 border-b border-gray-200">
                        <div class="flex items-center">
                            <img src="${product?.image || 'https://images.unsplash.com/photo-1509042239860-f550ce710b93?ixlib=rb-1.2.1&auto=format&fit=crop&w=100&q=80'}" 
                                 alt="${product?.name || 'Product'}" 
                                 class="w-16 h-16 object-cover rounded-lg mr-4">
                            <div>
                                <h4 class="font-bold">${product?.name || 'Unknown Product'}</h4>
                                <p class="text-gray-600">${orderingApp.formatPrice(product?.price || 0)}</p>
                            </div>
                        </div>
                        <div class="flex items-center">
                            <button onclick="orderingApp.updateCartQuantity(${item.id}, ${item.quantity - 1}); window.productsPage.showCartModal();" 
                                    class="bg-gray-200 hover:bg-gray-300 text-gray-700 px-2 py-1 rounded">
                                <i class="fas fa-minus"></i>
                            </button>
                            <span class="mx-3 font-bold">${item.quantity}</span>
                            <button onclick="orderingApp.updateCartQuantity(${item.id}, ${item.quantity + 1}); window.productsPage.showCartModal();" 
                                    class="bg-gray-200 hover:bg-gray-300 text-gray-700 px-2 py-1 rounded">
                                <i class="fas fa-plus"></i>
                            </button>
                            <button onclick="orderingApp.removeFromCart(${item.id}); window.productsPage.showCartModal();" 
                                    class="ml-3 text-red-500 hover:text-red-700">
                                <i class="fas fa-trash"></i>
                            </button>
                        </div>
                    </div>
                `;
            }).join('');
            
            const total = cart.reduce((sum, item) => {
                const product = this.products.find(p => p.id === item.id);
                return sum + (product?.price || 0) * item.quantity;
            }, 0);
            
            cartTotal.textContent = orderingApp.formatPrice(total);
        }
        
        modal.classList.remove('hidden');
    }

    closeCartModal() {
        const modal = document.getElementById('cartModal');
        modal.classList.add('hidden');
    }

    checkout() {
        const cart = orderingApp.getCart();
        if (cart.length === 0) {
            orderingApp.showAlert('Your cart is empty!', 'warning');
            return;
        }
        
        // Simple checkout simulation
        orderingApp.showAlert('Order placed successfully! Thank you for your purchase.', 'success');
        orderingApp.clearCart();
        this.closeCartModal();
    }

    showLoading(containerId) {
        const container = document.getElementById(containerId);
        if (container) {
            container.innerHTML = `
                <div class="col-span-full flex justify-center items-center py-20">
                    <div class="text-center">
                        <div class="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-[#c08450] mb-4"></div>
                        <p class="text-gray-600">Loading products...</p>
                    </div>
                </div>
            `;
        }
    }
}

// Initialize products page when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    if (document.getElementById('productsContainer')) {
        window.productsPage = new ProductsPage();
    }
});
