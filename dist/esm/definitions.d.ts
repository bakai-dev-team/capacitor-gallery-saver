export interface GallerySaverPlugin {
    /**
     * Сохраняет файл в галерею.
     * Если uri указывает на PDF — рендерит первую страницу в JPEG и сохраняет.
     * Если uri указывает на JPEG/PNG — сохраняет как есть.
     * @param uri file://... путь из Capacitor Filesystem
     * @param album альбом/папка (опц.)
     */
    saveAuto(options: {
        uri: string;
        album?: string;
    }): Promise<{
        saved: boolean;
        uri?: string;
    }>;
}
