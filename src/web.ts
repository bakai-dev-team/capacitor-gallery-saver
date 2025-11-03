import { WebPlugin } from '@capacitor/core';

import type { GallerySaverPlugin } from './definitions';

export class GallerySaverWeb extends WebPlugin implements GallerySaverPlugin {
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}
