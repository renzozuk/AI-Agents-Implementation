CREATE TABLE IF NOT EXISTS mensagem (
                                        id UUID PRIMARY KEY,
                                        contrato_id VARCHAR(255) NOT NULL,
                                        destinatario VARCHAR(255) NOT NULL,
                                        assunto TEXT,
                                        corpo_mensagem TEXT,
                                        agent_callback_url VARCHAR(2048),
                                        respondido BOOLEAN DEFAULT FALSE,
                                        data_envio TIMESTAMP WITHOUT TIME ZONE NOT NULL,
                                        data_resposta TIMESTAMP WITHOUT TIME ZONE
);

CREATE INDEX IF NOT EXISTS idx_mensagem_contrato_id ON mensagem(contrato_id);
CREATE INDEX IF NOT EXISTS idx_mensagem_respondido ON mensagem(respondido);