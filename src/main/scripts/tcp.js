import tcp from 'k6/x/tcp';
import { check, sleep } from 'k6';




function normalizeFromSocket(buffer){
  let zero = buffer.indexOf(0);
  buffer.splice(zero, buffer.length - zero);
  let str = String.fromCharCode(...buffer);
  return JSON.parse(str);
}

export const options = {
  vus: 100,
  iterations: 1000,
};

const cardCount = 50;
const operationCount = 1000;


//creating cards for tests
export function setup() {
    let cards = [];
    let url = "localhost:8181"
    const conn = tcp.connect(url);
    for (let i = 0; i < cardCount; i++){
    
        let str = `"client ${i}"\n`;
        //console.log(str);
        tcp.write(conn, str);
        let res = tcp.read(conn, 1024);
        let card = normalizeFromSocket(res);
        cards[i] = card.id;
        //console.log(card.id);
    }
    tcp.close(conn);
    return cards;
}



//execute test
export default function (cards) {
   let url = "localhost:8181"
   const conn = tcp.connect(url);
    for (let i = 0; i < cardCount; i++){
        let operations = [];
        for (let j = 0; j < operationCount; j++){
            operations[j] = {
                cardId: cards[i],
                operationDateTime: '2023-10-14T17:42:13.142388300',
                amount: 1 //j/100
            }
        }
        let payload = {
            operationList: operations
        }
        
        payload = JSON.stringify(payload)+'\n';
        //console.log(payload);
        tcp.write(conn, payload);
        let res = tcp.read(conn, 1024);
        //console.log(`send operations for ${i}`);
    }
    tcp.close(conn);
}

//check results
export function teardown(cards) {
     const expectedValue = 1 * operationCount * options.iterations;
     const rqs = cardCount * options.iterations;
     let url = "localhost:8181"
     const conn = tcp.connect(url);


    for (let i = 0; i <cardCount; i++){
      let payload = JSON.stringify(cards[i])+'\n';
      //console.log(payload);
      tcp.write(conn, payload);
      let res = tcp.read(conn, 1024);
      let card = normalizeFromSocket(res);
      //console.log(`expected:${expectedValue}, real:${card.ballance}`);
      check(res, {'is expected': (res) => card.ballance === expectedValue});

    }
    tcp.close(conn);

}


