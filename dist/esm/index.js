import { registerPlugin } from '@capacitor/core';
export const GallerySaver = registerPlugin('GallerySaver', {
    web: () => import('./web').then(m => new m.GallerySaverWeb()),
});
//# sourceMappingURL=index.js.map