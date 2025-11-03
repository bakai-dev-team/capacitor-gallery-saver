# gallery-saver

Save images to gallery

## Install

```bash
npm install gallery-saver
npx cap sync
```

## API

<docgen-index>

* [`saveAuto(...)`](#saveauto)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### saveAuto(...)

```typescript
saveAuto(options: { uri: string; album?: string; }) => Promise<{ saved: boolean; uri?: string; }>
```

Сохраняет файл в галерею.
Если uri указывает на PDF — рендерит первую страницу в JPEG и сохраняет.
Если uri указывает на JPEG/PNG — сохраняет как есть.

| Param         | Type                                          |
| ------------- | --------------------------------------------- |
| **`options`** | <code>{ uri: string; album?: string; }</code> |

**Returns:** <code>Promise&lt;{ saved: boolean; uri?: string; }&gt;</code>

--------------------

</docgen-api>
