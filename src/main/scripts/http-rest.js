import http from 'k6/http';
import { check, sleep } from 'k6';


export const options = {
    vus: 100,
    iterations: 1000,
};


const cardCount = 50;
const operationCount = 1000;
const params = {
    headers: {
        'Content-Type': 'application/json',
      },
};

//creating cards for tests
export function setup() {
    let cards = [];
    let url = "http://localhost:8080/cards"
    
    for (let i = 0; i < cardCount; i++){
        let payload = `client ${i}`;
        let  result = http.post(url, payload, params).json();
        cards[i] = result.id;
    }

    return cards;
}



//execute test
export default function (cards) {
   let url = "http://localhost:8080/cardoperations"

    for (let i = 0; i < cardCount; i++){
        let operations = [];
        for (let j = 0; j < operationCount; j++){
            operations[j] = {
                cardId: cards[i],
                LocalDateTime: '2023-10-13',
                amount: 1 //j/100
            }
        }
        let payload = {
            operationList: operations
        }
        
        payload = JSON.stringify(payload);
        http.post(url, payload, params);
    }
}

//check results
export function teardown(cards) {
    const expectedValue = 1 * operationCount * options.iterations;
    const rqs = cardCount * options.iterations;

    for (let i = 0; i <cardCount; i++){
        let url = `http://localhost:8080/cards/${cards[i]}`;
        //console.log(url);
        const res = http.get(url).json();
        //console.log(`expected=${expectedValue}`);
        //console.log(res);
        check(res, {'is expected': (res) => res.ballance === expectedValue});

    }


}


