import { WebPlugin } from '@capacitor/core';
export class GallerySaverWeb extends WebPlugin {
    async saveAuto() {
        console.warn('GallerySaver.saveAuto() не поддерживается в Web');
        return { saved: false };
    }
}
//# sourceMappingURL=web.js.map