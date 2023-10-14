import tcp from 'k6/x/tcp';
import { check } from 'k6';


export default function () {
    function normalizeFromSocket(buffer){
        let zero = buffer.indexOf(0);
        buffer.splice(zero, buffer.length - zero);
        let str = String.fromCharCode(...buffer);
        return JSON.parse(str);
    }


    let cardCount = 50;

    const conn = tcp.connect('localhost:8181');
    for (let i = 0; i < cardCount; i++){
    
        let str = `client ${i}\n`;
        //console.log(str);
        tcp.write(conn, str);
        let res = tcp.read(conn, 1024);
        let card = normalizeFromSocket(res);

        console.log(card.id);
    }
    tcp.close(conn);
}