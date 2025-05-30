-- Script para criar a tabela 'users'
CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       name VARCHAR(255),
                       email VARCHAR(255) UNIQUE NOT NULL,
                       password VARCHAR(255) NOT NULL, -- passwordHash mapeado para 'password'
                       phonenumber VARCHAR(255),
                       cpf VARCHAR(255),
                       date_of_birth DATE,
                       role VARCHAR(255) NOT NULL -- Mapeia o enum UserRole
);

-- Script para criar a tabela 'vehicles'
CREATE TABLE vehicles (
                          id BIGSERIAL PRIMARY KEY,
                          model VARCHAR(255),
                          brand VARCHAR(255),
                          year INT,
                          color VARCHAR(255),
                          price NUMERIC(19, 2), -- BigDecimal mapeado para NUMERIC
                          sold BOOLEAN DEFAULT FALSE,
                          vehicle_type VARCHAR(255),
                          vendor_id BIGINT NOT NULL,
                          CONSTRAINT fk_vendor
                              FOREIGN KEY (vendor_id)
                                  REFERENCES users (id)
                                  ON DELETE CASCADE -- Se o vendedor for excluído, seus veículos também serão
);

-- Script para criar a tabela 'vendor_sale'
CREATE TABLE vendor_sale (
                             id BIGSERIAL PRIMARY KEY,
                             vendor_id BIGINT NOT NULL,
                             buyer_id BIGINT NOT NULL,
                             vehicle_id BIGINT NOT NULL,
                             price DOUBLE PRECISION NOT NULL, -- Double mapeado para DOUBLE PRECISION
                             sale_date TIMESTAMP WITHOUT TIME ZONE NOT NULL, -- LocalDateTime mapeado para TIMESTAMP
                             CONSTRAINT fk_sale_vendor
                                 FOREIGN KEY (vendor_id)
                                     REFERENCES users (id)
                                     ON DELETE RESTRICT, -- Não permite excluir vendedor se houver vendas associadas
                             CONSTRAINT fk_sale_buyer
                                 FOREIGN KEY (buyer_id)
                                     REFERENCES users (id)
                                     ON DELETE RESTRICT, -- Não permite excluir comprador se houver vendas associadas
                             CONSTRAINT fk_sale_vehicle
                                 FOREIGN KEY (vehicle_id)
                                     REFERENCES vehicles (id)
                                     ON DELETE RESTRICT, -- Não permite excluir veículo se houver vendas associadas
                             CONSTRAINT unique_vehicle_sale UNIQUE (vehicle_id) -- Garante que um veículo só pode ser vendido uma vez
);

-- Script para criar a tabela 'purchase_request' (NOVA TABELA)
CREATE TABLE purchase_request (
                                  id BIGSERIAL PRIMARY KEY,
                                  vehicle_id BIGINT NOT NULL,
                                  buyer_id BIGINT NOT NULL,
                                  vendor_id BIGINT NOT NULL,
                                  request_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
                                  status VARCHAR(255) NOT NULL, -- Ex: 'PENDING', 'ACCEPTED', 'DENIED'
                                  response_date TIMESTAMP WITHOUT TIME ZONE, -- Data de aceitação/negação (pode ser nula)
                                  CONSTRAINT fk_pr_vehicle
                                      FOREIGN KEY (vehicle_id)
                                          REFERENCES vehicles (id)
                                          ON DELETE RESTRICT, -- Não permite excluir veículo se houver solicitações pendentes/aceitas
                                  CONSTRAINT fk_pr_buyer
                                      FOREIGN KEY (buyer_id)
                                          REFERENCES users (id)
                                          ON DELETE RESTRICT,
                                  CONSTRAINT fk_pr_vendor
                                      FOREIGN KEY (vendor_id)
                                          REFERENCES users (id)
                                          ON DELETE RESTRICT,
                                  CONSTRAINT unique_pending_request_for_vehicle UNIQUE (vehicle_id, status) WHERE status = 'PENDING'
);

-- Script para criar a tabela de junção 'favorites' (ManyToMany entre User e Vehicle)
CREATE TABLE favorites (
                           user_id BIGINT NOT NULL,
                           vehicle_id BIGINT NOT NULL,
                           PRIMARY KEY (user_id, vehicle_id),
                           CONSTRAINT fk_favorites_user
                               FOREIGN KEY (user_id)
                                   REFERENCES users (id)
                                   ON DELETE CASCADE,
                           CONSTRAINT fk_favorites_vehicle
                               FOREIGN KEY (vehicle_id)
                                   REFERENCES vehicles (id)
                                   ON DELETE CASCADE
);

-- Script para criar a tabela de coleção 'vehicle_photos' (ElementCollection em Vehicle)
CREATE TABLE vehicle_photos (
                                vehicle_id BIGINT NOT NULL,
                                photo_url VARCHAR(255) NOT NULL,
                                PRIMARY KEY (vehicle_id, photo_url), -- Chave primária composta para garantir unicidade da URL por veículo
                                CONSTRAINT fk_vehicle_photos_vehicle
                                    FOREIGN KEY (vehicle_id)
                                        REFERENCES vehicles (id)
                                        ON DELETE CASCADE
);