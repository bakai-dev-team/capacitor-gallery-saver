import { GallerySaver } from 'gallery-saver';

window.testEcho = () => {
    const inputValue = document.getElementById("echoInput").value;
    GallerySaver.echo({ value: inputValue })
}
