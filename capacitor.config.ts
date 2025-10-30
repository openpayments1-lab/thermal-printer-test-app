import { CapacitorConfig } from '@capacitor/cli';

const config: CapacitorConfig = {
  appId: 'com.thermalprinter.test',
  appName: 'Thermal Printer Test',
  webDir: 'src',
  server: {
    androidScheme: 'https'
  }
};

export default config;
