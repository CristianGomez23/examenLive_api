CREATE SEQUENCE IF NOT EXISTS ids_globales_seq START WITH 1 INCREMENT BY 1;

CREATE TEMPORARY TABLE uuid_ids_migracion (
    id_anterior UUID PRIMARY KEY,
    id_numerico BIGINT NOT NULL UNIQUE
) ON COMMIT DROP;

DO $$
DECLARE
    referencia RECORD;
BEGIN
    FOR referencia IN
        SELECT * FROM (VALUES
            ('preguntas', 'id'),
            ('pregunta_respuestas_correctas', 'pregunta_id'),
            ('resultados', 'id'),
            ('resultados', 'sesion_id'),
            ('resultados', 'estudiante_id')
        ) AS columnas(tabla, columna)
    LOOP
        IF EXISTS (
            SELECT 1
            FROM information_schema.columns
            WHERE table_schema = 'public'
              AND table_name = referencia.tabla
              AND column_name = referencia.columna
              AND data_type = 'uuid'
        ) THEN
            EXECUTE format(
                'INSERT INTO pg_temp.uuid_ids_migracion (id_anterior, id_numerico) '
                'SELECT %1$I, nextval(''ids_globales_seq'') FROM %2$I WHERE %1$I IS NOT NULL '
                'ON CONFLICT (id_anterior) DO NOTHING',
                referencia.columna,
                referencia.tabla
            );
        END IF;
    END LOOP;

    IF to_regclass('public.pregunta_respuestas_correctas') IS NOT NULL THEN
        FOR referencia IN
            SELECT conname
            FROM pg_constraint
            WHERE conrelid = 'public.pregunta_respuestas_correctas'::regclass
              AND contype = 'f'
        LOOP
            EXECUTE format(
                'ALTER TABLE pregunta_respuestas_correctas DROP CONSTRAINT %I',
                referencia.conname
            );
        END LOOP;
    END IF;

    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'public' AND table_name = 'preguntas'
          AND column_name = 'id' AND data_type = 'uuid'
    ) THEN
        ALTER TABLE preguntas ADD COLUMN id_numerico BIGINT;
        UPDATE preguntas pregunta
        SET id_numerico = migracion.id_numerico
        FROM uuid_ids_migracion migracion
        WHERE pregunta.id = migracion.id_anterior;
        ALTER TABLE preguntas DROP CONSTRAINT IF EXISTS preguntas_pkey;
        ALTER TABLE preguntas DROP COLUMN id;
        ALTER TABLE preguntas RENAME COLUMN id_numerico TO id;
        ALTER TABLE preguntas ADD CONSTRAINT preguntas_pkey PRIMARY KEY (id);
    END IF;

    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'public' AND table_name = 'pregunta_respuestas_correctas'
          AND column_name = 'pregunta_id' AND data_type = 'uuid'
    ) THEN
        ALTER TABLE pregunta_respuestas_correctas ADD COLUMN pregunta_id_numerico BIGINT;
        UPDATE pregunta_respuestas_correctas opcion
        SET pregunta_id_numerico = migracion.id_numerico
        FROM uuid_ids_migracion migracion
        WHERE opcion.pregunta_id = migracion.id_anterior;
        ALTER TABLE pregunta_respuestas_correctas
            DROP CONSTRAINT IF EXISTS pregunta_respuestas_correctas_pkey;
        ALTER TABLE pregunta_respuestas_correctas DROP COLUMN pregunta_id;
        ALTER TABLE pregunta_respuestas_correctas
            RENAME COLUMN pregunta_id_numerico TO pregunta_id;
        ALTER TABLE pregunta_respuestas_correctas
            ADD CONSTRAINT pregunta_respuestas_correctas_pkey PRIMARY KEY (pregunta_id, respuesta);
        ALTER TABLE pregunta_respuestas_correctas
            ADD CONSTRAINT pregunta_respuestas_correctas_pregunta_fk
            FOREIGN KEY (pregunta_id) REFERENCES preguntas(id);
    END IF;

    IF to_regclass('public.resultados') IS NOT NULL THEN
        IF EXISTS (
            SELECT 1 FROM information_schema.columns
            WHERE table_schema = 'public' AND table_name = 'resultados'
              AND column_name = 'id' AND data_type = 'uuid'
        ) THEN
            ALTER TABLE resultados DROP CONSTRAINT IF EXISTS resultados_pkey;
            ALTER TABLE resultados ADD COLUMN id_numerico BIGINT;
            UPDATE resultados resultado
            SET id_numerico = migracion.id_numerico
            FROM uuid_ids_migracion migracion
            WHERE resultado.id = migracion.id_anterior;
            ALTER TABLE resultados DROP COLUMN id;
            ALTER TABLE resultados RENAME COLUMN id_numerico TO id;
            ALTER TABLE resultados ADD CONSTRAINT resultados_pkey PRIMARY KEY (id);
        END IF;

        IF EXISTS (
            SELECT 1 FROM information_schema.columns
            WHERE table_schema = 'public' AND table_name = 'resultados'
              AND column_name = 'sesion_id' AND data_type = 'uuid'
        ) THEN
            ALTER TABLE resultados ADD COLUMN sesion_id_numerico BIGINT;
            UPDATE resultados resultado
            SET sesion_id_numerico = migracion.id_numerico
            FROM uuid_ids_migracion migracion
            WHERE resultado.sesion_id = migracion.id_anterior;
            ALTER TABLE resultados DROP COLUMN sesion_id;
            ALTER TABLE resultados RENAME COLUMN sesion_id_numerico TO sesion_id;
        END IF;

        IF EXISTS (
            SELECT 1 FROM information_schema.columns
            WHERE table_schema = 'public' AND table_name = 'resultados'
              AND column_name = 'estudiante_id' AND data_type = 'uuid'
        ) THEN
            ALTER TABLE resultados ADD COLUMN estudiante_id_numerico BIGINT;
            UPDATE resultados resultado
            SET estudiante_id_numerico = migracion.id_numerico
            FROM uuid_ids_migracion migracion
            WHERE resultado.estudiante_id = migracion.id_anterior;
            ALTER TABLE resultados DROP COLUMN estudiante_id;
            ALTER TABLE resultados RENAME COLUMN estudiante_id_numerico TO estudiante_id;
        END IF;
    END IF;
END $$;