// Manual plugin registration for inline Capacitor plugin
if (typeof Capacitor !== 'undefined' && Capacitor.registerPlugin) {
    window.ThermalPrinter = Capacitor.registerPlugin('ThermalPrinter');
}

class ThermalPrinterApp {
    constructor() {
        this.isConnected = false;
        this.cart = [];
        this.displayInterval = null;
        this.currentImageIndex = 0;
        this.displayActive = false;
        this.selectedDevice = null;
        
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
        this.log('Application initialized (Native Hardware Mode)', 'success');
        this.checkCapacitorAvailable();
    }
    
    checkCapacitorAvailable() {
        // Comprehensive debug logging
        console.log('=== CAPACITOR DEBUG INFO ===');
        console.log('Capacitor defined?', typeof Capacitor !== 'undefined');
        
        if (typeof Capacitor !== 'undefined') {
            console.log('Capacitor.Plugins:', Capacitor.Plugins);
            console.log('Available plugins:', Object.keys(Capacitor.Plugins || {}));
            console.log('ThermalPrinter plugin:', Capacitor.Plugins?.ThermalPrinter);
            console.log('window.ThermalPrinter:', window.ThermalPrinter);
        }
        
        console.log('=== END DEBUG ===');
        
        if (this.isNativePluginAvailable()) {
            this.log('✓ Capacitor detected - Hardware features enabled', 'success');
            this.log('✓ ThermalPrinter plugin found and ready', 'success');
        } else {
            this.log('⚠ Running in browser - Hardware features unavailable (simulation mode only)', 'info');
            if (typeof Capacitor !== 'undefined') {
                this.log('ℹ Capacitor is available but ThermalPrinter plugin not found', 'error');
                this.log('ℹ Available plugins: ' + Object.keys(Capacitor.Plugins || {}).join(', '), 'info');
            } else {
                this.log('ℹ Capacitor not available - you are in a web browser', 'info');
            }
        }
    }
    
    isNativePluginAvailable() {
        // Check if Capacitor is defined and has our plugin
        if (typeof Capacitor === 'undefined') {
            return false;
        }
        
        // Check for the plugin in Capacitor.Plugins
        return Capacitor.Plugins && Capacitor.Plugins.ThermalPrinter !== undefined;
    }
    
    getPlugin() {
        // Return the plugin from either location
        return window.ThermalPrinter || (typeof Capacitor !== 'undefined' && Capacitor.Plugins && Capacitor.Plugins.ThermalPrinter);
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
                this.log('Scanning for USB printers...');
                
                if (this.isNativePluginAvailable()) {
                    const plugin = this.getPlugin();
                    const result = await plugin.listUsbDevices();
                    this.log(`Found ${result.devices.length} USB device(s)`);
                    
                    if (result.devices.length === 0) {
                        this.log('✗ No USB devices found. Please connect your VOLCORA printer.', 'error');
                        return;
                    }
                    
                    result.devices.forEach((dev, idx) => {
                        const deviceClass = dev.deviceClass === 7 ? 'Printer' : dev.deviceClass === 0 ? 'Interface-defined' : `Class ${dev.deviceClass}`;
                        this.log(`Device ${idx + 1}: ${dev.manufacturerName || 'Unknown'} ${dev.productName || ''} (VID: 0x${dev.vendorId.toString(16).toUpperCase()}, PID: 0x${dev.productId.toString(16).toUpperCase()}) - ${deviceClass}`);
                    });
                    
                    // Try to find a printer device (class 7) first
                    let device = result.devices.find(d => d.deviceClass === 7);
                    if (!device) {
                        // Fallback to first device
                        device = result.devices[0];
                        this.log('⚠ No device with Printer class (7) found, using first device', 'info');
                    } else {
                        this.log(`Selected printer device: ${device.manufacturerName || 'Unknown'}`, 'info');
                    }
                    this.selectedDevice = device;
                    
                    this.log(`Connecting to ${device.manufacturerName || 'USB'} printer...`);
                    
                    const connectResult = await plugin.connectToPrinter({
                        vendorId: device.vendorId,
                        productId: device.productId
                    });
                    
                    this.isConnected = true;
                    btn.textContent = 'Disconnect';
                    statusText.textContent = 'Connected';
                    indicator.classList.add('connected');
                    
                    this.log(`✓ ${connectResult.message}`, 'success');
                    this.log(`Device: ${device.manufacturerName || 'USB Printer'} ${device.productName || ''}`, 'info');
                } else {
                    this.log('✗ Native plugin not available. Build APK and install on Android device for hardware access.', 'error');
                }
            } catch (error) {
                this.log(`✗ Connection failed: ${error}`, 'error');
            }
        } else {
            try {
                if (this.isNativePluginAvailable()) {
                    const plugin = this.getPlugin();
                    await plugin.disconnectPrinter();
                }
                
                this.isConnected = false;
                this.selectedDevice = null;
                btn.textContent = 'Connect Printer';
                statusText.textContent = 'Disconnected';
                indicator.classList.remove('connected');
                
                this.log('Printer disconnected', 'info');
            } catch (error) {
                this.log(`✗ Disconnect error: ${error}`, 'error');
            }
        }
    }
    
    async sendToPrinter(data) {
        if (!this.isConnected) {
            this.log('✗ Printer not connected', 'error');
            return false;
        }
        
        if (this.isNativePluginAvailable()) {
            try {
                const encoder = new TextEncoder();
                const byteArray = encoder.encode(data);
                
                let binary = '';
                for (let i = 0; i < byteArray.length; i++) {
                    binary += String.fromCharCode(byteArray[i]);
                }
                const base64Data = btoa(binary);
                
                const plugin = this.getPlugin();
                const result = await plugin.printRawData({
                    data: base64Data
                });
                
                this.log(`✓ Sent ${result.bytesTransferred} bytes to printer`, 'success');
                return true;
            } catch (error) {
                this.log(`✗ Print error: ${error}`, 'error');
                return false;
            }
        } else {
            this.log('✗ Native plugin not available. Build APK and install on Android device.', 'error');
            return false;
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
    
    async toggleDisplay() {
        const btn = document.getElementById('toggleDisplayBtn');
        
        if (!this.displayActive) {
            try {
                if (this.isNativePluginAvailable()) {
                    this.log('Checking for secondary display...');
                    
                    const plugin = this.getPlugin();
                    const displayCheck = await plugin.checkSecondaryDisplay();
                    
                    if (!displayCheck.hasSecondaryDisplay) {
                        this.log('✗ No secondary display detected. Connect a customer display.', 'error');
                        this.log('  Falling back to in-app simulation...', 'info');
                        this.showSimulatedDisplay();
                        return;
                    }
                    
                    this.log(`✓ Secondary display found: ${displayCheck.secondaryDisplayInfo.name}`, 'success');
                    
                    const html = this.generateCustomerDisplayHTML();
                    await plugin.showOnSecondaryDisplay({ html });
                    
                    this.displayActive = true;
                    btn.textContent = 'Hide Customer Display';
                    this.startSlideshow();
                    this.log('✓ Customer display activated on secondary screen', 'success');
                } else {
                    this.log('⚠ Running in browser, showing simulated display', 'info');
                    this.showSimulatedDisplay();
                }
            } catch (error) {
                this.log(`✗ Display error: ${error}`, 'error');
            }
        } else {
            try {
                if (this.isNativePluginAvailable()) {
                    const plugin = this.getPlugin();
                    await plugin.hideSecondaryDisplay();
                }
                
                this.displayActive = false;
                btn.textContent = 'Toggle Customer Display';
                this.stopSlideshow();
                this.hideSimulatedDisplay();
                this.log('Customer display disabled', 'info');
            } catch (error) {
                this.log(`✗ Error hiding display: ${error}`, 'error');
            }
        }
    }
    
    showSimulatedDisplay() {
        const display = document.getElementById('customerDisplay');
        const btn = document.getElementById('toggleDisplayBtn');
        display.style.display = 'block';
        btn.textContent = 'Hide Customer Display';
        this.displayActive = true;
        this.startSlideshow();
    }
    
    hideSimulatedDisplay() {
        const display = document.getElementById('customerDisplay');
        display.style.display = 'none';
    }
    
    generateCustomerDisplayHTML() {
        const currentImage = this.images[this.currentImageIndex];
        return `
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <style>
        body {
            margin: 0;
            padding: 0;
            background: #000;
            display: flex;
            align-items: center;
            justify-content: center;
            height: 100vh;
            overflow: hidden;
        }
        img {
            max-width: 100%;
            max-height: 100vh;
            object-fit: contain;
        }
        .text-overlay {
            position: absolute;
            bottom: 40px;
            left: 0;
            right: 0;
            text-align: center;
            color: white;
            font-size: 48px;
            font-weight: bold;
            text-shadow: 2px 2px 8px rgba(0,0,0,0.8);
            font-family: Arial, sans-serif;
        }
    </style>
</head>
<body>
    <img src="${currentImage}" alt="Product">
    <div class="text-overlay">Welcome! Thank you for shopping with us.</div>
</body>
</html>
        `;
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
