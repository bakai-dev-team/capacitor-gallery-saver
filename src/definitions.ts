export interface GallerySaverPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
