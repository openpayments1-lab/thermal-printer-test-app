class ThermalPrinterApp {
    constructor() {
        this.isConnected = false;
        this.cart = [];
        this.displayInterval = null;
        this.currentImageIndex = 0;
        
        this.images = [
            'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=800',
            'https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=800',
            'https://images.unsplash.com/photo-1526170375885-4d8ecf77b99f?w=800',
            'https://images.unsplash.com/photo-1572635196237-14b3f281503f?w=800',
            'https://images.unsplash.com/photo-1491553895911-0055eca6402d?w=800'
        ];
        
        this.init();
    }
    
    init() {
        this.setupEventListeners();
        this.log('Application initialized', 'success');
    }
    
    setupEventListeners() {
        document.getElementById('connectBtn').addEventListener('click', () => this.toggleConnection());
        document.getElementById('testPrintBtn').addEventListener('click', () => this.printTestReceipt());
        document.getElementById('selfTestBtn').addEventListener('click', () => this.printerSelfTest());
        document.getElementById('addItemBtn').addEventListener('click', () => this.addItem());
        document.getElementById('clearCartBtn').addEventListener('click', () => this.clearCart());
        document.getElementById('previewBtn').addEventListener('click', () => this.showPreview());
        document.getElementById('printReceiptBtn').addEventListener('click', () => this.printReceipt());
        document.getElementById('toggleDisplayBtn').addEventListener('click', () => this.toggleDisplay());
        document.getElementById('closePreview').addEventListener('click', () => this.closePreview());
        document.getElementById('printFromPreview').addEventListener('click', () => {
            this.closePreview();
            this.printReceipt();
        });
        
        document.getElementById('itemName').addEventListener('keypress', (e) => {
            if (e.key === 'Enter') this.addItem();
        });
    }
    
    log(message, type = 'info') {
        const logOutput = document.getElementById('logOutput');
        const time = new Date().toLocaleTimeString();
        const entry = document.createElement('div');
        entry.className = 'log-entry';
        
        const typeClass = type === 'success' ? 'log-success' : type === 'error' ? 'log-error' : '';
        
        entry.innerHTML = `
            <span class="log-time">[${time}]</span>
            <span class="log-message ${typeClass}">${message}</span>
        `;
        
        logOutput.appendChild(entry);
        logOutput.scrollTop = logOutput.scrollHeight;
    }
    
    async toggleConnection() {
        const btn = document.getElementById('connectBtn');
        const indicator = document.getElementById('statusIndicator');
        const statusText = document.getElementById('statusText');
        
        if (!this.isConnected) {
            try {
                this.log('Attempting to connect to USB printer...');
                
                if ('usb' in navigator) {
                    const device = await navigator.usb.requestDevice({
                        filters: [{ vendorId: 0x0DD4 }]
                    });
                    
                    await device.open();
                    if (device.configuration === null) {
                        await device.selectConfiguration(1);
                    }
                    await device.claimInterface(0);
                    
                    this.device = device;
                    this.isConnected = true;
                    
                    btn.textContent = 'Disconnect';
                    statusText.textContent = 'Connected';
                    indicator.classList.add('connected');
                    
                    this.log('✓ USB printer connected successfully', 'success');
                } else {
                    this.isConnected = true;
                    btn.textContent = 'Disconnect';
                    statusText.textContent = 'Connected (Simulation)';
                    indicator.classList.add('connected');
                    
                    this.log('✓ Printer connected (simulation mode)', 'success');
                    this.log('Note: USB API not available in browser. Deploy to Android for real USB connection.', 'info');
                }
            } catch (error) {
                this.log(`✗ Connection failed: ${error.message}`, 'error');
            }
        } else {
            if (this.device) {
                try {
                    await this.device.close();
                } catch (e) {
                    console.error('Error closing device:', e);
                }
            }
            
            this.isConnected = false;
            this.device = null;
            btn.textContent = 'Connect Printer';
            statusText.textContent = 'Disconnected';
            indicator.classList.remove('connected');
            
            this.log('Printer disconnected', 'info');
        }
    }
    
    async sendToPrinter(data) {
        if (!this.isConnected) {
            this.log('✗ Printer not connected', 'error');
            return false;
        }
        
        if (this.device) {
            try {
                const encoder = new TextEncoder();
                const encoded = encoder.encode(data);
                await this.device.transferOut(1, encoded);
                return true;
            } catch (error) {
                this.log(`✗ Print error: ${error.message}`, 'error');
                return false;
            }
        } else {
            this.log('→ Sending to printer (simulation)', 'info');
            console.log('Print data:', data);
            return true;
        }
    }
    
    generateESCPOS(text) {
        const ESC = '\x1B';
        const GS = '\x1D';
        
        let commands = '';
        commands += ESC + '@';
        commands += text;
        commands += '\n\n\n';
        commands += GS + 'V' + '\x41' + '\x03';
        
        return commands;
    }
    
    async printTestReceipt() {
        this.log('Printing test receipt...');
        
        const receipt = `
================================
      THERMAL PRINTER TEST
================================

Date: ${new Date().toLocaleString()}
Test ID: ${Math.random().toString(36).substr(2, 9).toUpperCase()}

--------------------------------
PRINTER INFORMATION
--------------------------------
Model: VOLCORA Thermal Printer
Paper: 80mm (72mm print width)
Speed: 220mm/s
Interface: USB (ESC/POS)

--------------------------------
TEST PATTERNS
--------------------------------
ABCDEFGHIJKLMNOPQRSTUVWXYZ
abcdefghijklmnopqrstuvwxyz
0123456789 !@#$%^&*()

--------------------------------
LINE QUALITY TEST
--------------------------------
████████████████████████████
▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓
▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒
░░░░░░░░░░░░░░░░░░░░░░░░░░░░

================================
       TEST SUCCESSFUL
================================

        `;
        
        const success = await this.sendToPrinter(this.generateESCPOS(receipt));
        if (success) {
            this.log('✓ Test receipt printed', 'success');
        }
    }
    
    async printerSelfTest() {
        this.log('Initiating printer self-test...');
        this.log('Note: Self-test is typically triggered by holding the feed button while powering on', 'info');
        
        const ESC = '\x1B';
        const selfTestCommand = ESC + 'T';
        
        const success = await this.sendToPrinter(selfTestCommand);
        if (success) {
            this.log('✓ Self-test command sent', 'success');
        }
    }
    
    addItem() {
        const nameInput = document.getElementById('itemName');
        const priceInput = document.getElementById('itemPrice');
        const qtyInput = document.getElementById('itemQty');
        
        const name = nameInput.value.trim();
        const price = parseFloat(priceInput.value) || 0;
        const qty = parseInt(qtyInput.value) || 1;
        
        if (!name || price <= 0) {
            this.log('✗ Please enter valid item name and price', 'error');
            return;
        }
        
        const item = {
            id: Date.now(),
            name,
            price,
            qty,
            total: price * qty
        };
        
        this.cart.push(item);
        this.renderCart();
        this.updateTotals();
        
        nameInput.value = '';
        priceInput.value = '';
        qtyInput.value = '1';
        nameInput.focus();
        
        this.log(`✓ Added: ${name} x${qty} @ $${price.toFixed(2)}`, 'success');
    }
    
    removeItem(id) {
        const item = this.cart.find(i => i.id === id);
        if (item) {
            this.log(`Removed: ${item.name}`, 'info');
        }
        this.cart = this.cart.filter(i => i.id !== id);
        this.renderCart();
        this.updateTotals();
    }
    
    clearCart() {
        if (this.cart.length === 0) return;
        
        this.cart = [];
        this.renderCart();
        this.updateTotals();
        this.log('Cart cleared', 'info');
    }
    
    renderCart() {
        const cartContainer = document.getElementById('cartItems');
        
        if (this.cart.length === 0) {
            cartContainer.innerHTML = '<p style="text-align: center; color: #94a3b8; padding: 20px;">Cart is empty</p>';
            return;
        }
        
        cartContainer.innerHTML = this.cart.map(item => `
            <div class="cart-item">
                <div class="cart-item-info">
                    <div class="cart-item-name">${item.name}</div>
                    <div class="cart-item-details">
                        $${item.price.toFixed(2)} × ${item.qty}
                    </div>
                </div>
                <div class="cart-item-total">$${item.total.toFixed(2)}</div>
                <button class="remove-btn" onclick="app.removeItem(${item.id})">Remove</button>
            </div>
        `).join('');
    }
    
    updateTotals() {
        const subtotal = this.cart.reduce((sum, item) => sum + item.total, 0);
        const tax = subtotal * 0.10;
        const total = subtotal + tax;
        
        document.getElementById('subtotal').textContent = `$${subtotal.toFixed(2)}`;
        document.getElementById('tax').textContent = `$${tax.toFixed(2)}`;
        document.getElementById('total').textContent = `$${total.toFixed(2)}`;
    }
    
    generateReceiptText() {
        const subtotal = this.cart.reduce((sum, item) => sum + item.total, 0);
        const tax = subtotal * 0.10;
        const total = subtotal + tax;
        const now = new Date();
        
        let receipt = '';
        receipt += '================================\n';
        receipt += '       YOUR BUSINESS NAME\n';
        receipt += '================================\n';
        receipt += `Date: ${now.toLocaleDateString()}\n`;
        receipt += `Time: ${now.toLocaleTimeString()}\n`;
        receipt += `Receipt #: ${Math.random().toString(36).substr(2, 9).toUpperCase()}\n`;
        receipt += '--------------------------------\n';
        receipt += 'ITEMS\n';
        receipt += '--------------------------------\n';
        
        this.cart.forEach(item => {
            const nameLine = item.name.padEnd(20);
            const totalStr = `$${item.total.toFixed(2)}`.padStart(8);
            receipt += `${nameLine}${totalStr}\n`;
            receipt += `  $${item.price.toFixed(2)} x ${item.qty}\n`;
        });
        
        receipt += '--------------------------------\n';
        receipt += `Subtotal:$${subtotal.toFixed(2).padStart(18)}\n`;
        receipt += `Tax(10%):$${tax.toFixed(2).padStart(18)}\n`;
        receipt += '================================\n';
        receipt += `TOTAL:   $${total.toFixed(2).padStart(18)}\n`;
        receipt += '================================\n';
        receipt += '\n';
        receipt += '    Thank you for your business!\n';
        receipt += '\n';
        
        return receipt;
    }
    
    showPreview() {
        if (this.cart.length === 0) {
            this.log('✗ Cart is empty', 'error');
            return;
        }
        
        const modal = document.getElementById('previewModal');
        const preview = document.getElementById('previewContent');
        
        preview.textContent = this.generateReceiptText();
        modal.classList.add('active');
        
        this.log('Showing receipt preview', 'info');
    }
    
    closePreview() {
        const modal = document.getElementById('previewModal');
        modal.classList.remove('active');
    }
    
    async printReceipt() {
        if (this.cart.length === 0) {
            this.log('✗ Cart is empty', 'error');
            return;
        }
        
        this.log('Printing receipt...');
        
        const receiptText = this.generateReceiptText();
        const success = await this.sendToPrinter(this.generateESCPOS(receiptText));
        
        if (success) {
            this.log('✓ Receipt printed successfully', 'success');
            
            setTimeout(() => {
                this.clearCart();
            }, 1000);
        }
    }
    
    toggleDisplay() {
        const display = document.getElementById('customerDisplay');
        const btn = document.getElementById('toggleDisplayBtn');
        
        if (display.style.display === 'none') {
            display.style.display = 'block';
            btn.textContent = 'Hide Customer Display';
            this.startSlideshow();
            this.log('✓ Customer display enabled', 'success');
        } else {
            display.style.display = 'none';
            btn.textContent = 'Toggle Customer Display';
            this.stopSlideshow();
            this.log('Customer display disabled', 'info');
        }
    }
    
    startSlideshow() {
        this.currentImageIndex = 0;
        this.updateSlideshow();
        
        this.displayInterval = setInterval(() => {
            this.currentImageIndex = (this.currentImageIndex + 1) % this.images.length;
            this.updateSlideshow();
        }, 3000);
    }
    
    updateSlideshow() {
        const img = document.getElementById('slideshowImage');
        img.src = this.images[this.currentImageIndex];
    }
    
    stopSlideshow() {
        if (this.displayInterval) {
            clearInterval(this.displayInterval);
            this.displayInterval = null;
        }
    }
}

const app = new ThermalPrinterApp();
