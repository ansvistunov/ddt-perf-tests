import {Client, Stream} from 'k6/experimental/grpc';
import { check, sleep } from 'k6';

export const options = {
    vus: 100,
    iterations: 1000,
  };


const cardCount = 50;
const operationCount = 1000;

const client = new Client();
client.load([], '../proto/BillingService.proto');

// //creating cards for tests
export function setup() {
    
    let cards = [];
    let url = "localhost:8282";

    client.connect(url, {
        plaintext: true
     });

    for (let i = 0; i < cardCount; i++){
        const data = {
            personname: `client ${i}`
        }
        const response = client.invoke('com.asw.billing.BillingServiceStream/addNewCard', data);
        //console.log(response.message);
        //const card = JSON.parse(response.message);
        //console.log(card);
        cards[i] = response.message.card;
        //console.log(cards[i]);
    }
    return cards;
    client.close();
    //sleep(1);
}


export default function (cards) {
  let url = "localhost:8282"
  client.connect(url, {
     plaintext: true
  });

  

  
  for (let i = 0; i < cardCount; i++){
    let operations = []
    for (let j = 0; j < operationCount; j++){
        operations[j] = {
            card: cards[i],
            datetime: '2023-10-14T17:42:13.142388300',
            money: 1 //j/100
        }
    }

    const data = {
        operations: operations
    }
    const response = client.invoke('com.asw.billing.BillingServiceStream/processOperations', data);

  }
  

};

//check results
export function teardown(cards) {
    const expectedValue = 1 * operationCount * options.iterations;
    const rqs = cardCount * options.iterations;

    let url = "localhost:8282"
    client.connect(url, {
       plaintext: true
    });

    for (let i = 0; i <cardCount; i++){
        const data = {
            card: cards[i]
        }
        const response = client.invoke('com.asw.billing.BillingServiceStream/getCardBalance', data);
        //console.log(`expected=${expectedValue}`);
        //console.log(res);
        check(response, {'is expected': (res) => res.message.balance === expectedValue});

    }
    client.close();

}