import { WebPlugin } from '@capacitor/core';
import type { GallerySaverPlugin } from './definitions';
export declare class GallerySaverWeb extends WebPlugin implements GallerySaverPlugin {
    saveAuto(): Promise<{
        saved: boolean;
        uri?: string;
    }>;
}
