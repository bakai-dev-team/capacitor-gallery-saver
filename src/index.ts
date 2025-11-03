import { registerPlugin } from '@capacitor/core';
import type { GallerySaverPlugin } from './definitions';

export const GallerySaver = registerPlugin<GallerySaverPlugin>('GallerySaver', {
  web: () => import('./web').then(m => new m.GallerySaverWeb()),
});
