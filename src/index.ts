import { registerPlugin } from '@capacitor/core';

import type { GallerySaverPlugin } from './definitions';

const GallerySaver = registerPlugin<GallerySaverPlugin>('GallerySaver', {
  web: () => import('./web').then((m) => new m.GallerySaverWeb()),
});

export * from './definitions';
export { GallerySaver };
