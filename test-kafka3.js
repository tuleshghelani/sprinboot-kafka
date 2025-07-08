const { Kafka, logLevel } = require('kafkajs');
const KAFKA_BOOTSTRAP_SERVERS = '';
const KAFKA_SASL_USERNAME = '';
const KAFKA_SASL_PASSWORD = '';
const KAFKA_GROUP_ID = 'local-consumer-group';
const KAFKA_TOPIC = '';
const KAFKA_AUTO_OFFSET_RESET = 'earliest';


(async () => {
  try {
    console.log('⏳ Step 1: Initializing Kafka client...');
    const kafka = new Kafka({
      clientId: 'my-kafka-client',
      brokers: [KAFKA_BOOTSTRAP_SERVERS],
      ssl: true,
      sasl: {
        mechanism: 'plain',
        username: KAFKA_SASL_USERNAME,
        password: KAFKA_SASL_PASSWORD,
      },
      loglevel:logLevel.INFO,
    });


    const consumer = kafka.consumer({ groupId: KAFKA_GROUP_ID });


    console.log('🔌 Step 2: Connecting to Kafka broker...');
    await consumer.connect();console.log('✅ Connected to Kafka broker');


    console.log(`📡 Step 3: Subscribing to topic "${KAFKA_TOPIC}"...`);
    await consumer.subscribe({ topic: KAFKA_TOPIC, fromBeginning: KAFKA_AUTO_OFFSET_RESET === 'earliest' });
    console.log(`✅ Subscribed to topic "${KAFKA_TOPIC}"`);


    console.log('▶️ Step 4: Listening for messag');
    await consumer.run({
      eachMessage: async ({ topic, partition, message }) => {
        const log = {
          topic,
          partition,
          offset: message.offset,
          key: message.key?.toString() || null,
          value: message.value?.toString() || null,
        };


        console.log('📥 Received message:', JSON.stringify(log, null, 2));
      },
    });
  } catch (error) {
    console.error('❌ Error occurred:', error);
  }
})();

