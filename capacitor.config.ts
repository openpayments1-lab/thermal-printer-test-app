import { CapacitorConfig } from '@capacitor/cli';

const config: CapacitorConfig = {
  appId: 'com.thermalprinter.test',
  appName: 'Thermal Printer Test',
  webDir: 'src',
  server: {
    androidScheme: 'https'
  },
  plugins: {
    ThermalPrinter: {
      androidClassName: 'com.thermalprinter.test.ThermalPrinterPlugin'
    }
  }
};

export default config;
