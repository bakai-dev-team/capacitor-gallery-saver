import { WebPlugin } from '@capacitor/core';
import type { GallerySaverPlugin } from './definitions';

export class GallerySaverWeb extends WebPlugin implements GallerySaverPlugin {
  async saveAuto(): Promise<{ saved: boolean; uri?: string }> {
    console.warn('GallerySaver.saveAuto() не поддерживается в Web');
    return { saved: false };
  }
}
