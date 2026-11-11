DO $$
BEGIN
    IF to_regclass('public.pregunta_respuestas_correctas') IS NOT NULL
       AND to_regclass('public.pregunta_opciones') IS NULL THEN
        ALTER TABLE pregunta_respuestas_correctas RENAME TO pregunta_opciones;
    END IF;

    IF to_regclass('public.pregunta_opciones') IS NOT NULL THEN
        IF EXISTS (
            SELECT 1 FROM information_schema.columns
            WHERE table_schema = 'public' AND table_name = 'pregunta_opciones'
              AND column_name = 'respuesta'
        ) THEN
            ALTER TABLE pregunta_opciones RENAME COLUMN respuesta TO texto;
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM information_schema.columns
            WHERE table_schema = 'public' AND table_name = 'pregunta_opciones'
              AND column_name = 'correcta'
        ) THEN
            ALTER TABLE pregunta_opciones
                ADD COLUMN correcta BOOLEAN NOT NULL DEFAULT TRUE;
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM information_schema.columns
            WHERE table_schema = 'public' AND table_name = 'pregunta_opciones'
              AND column_name = 'orden'
        ) THEN
            ALTER TABLE pregunta_opciones ADD COLUMN orden INTEGER;
            WITH opciones_ordenadas AS (
                SELECT pregunta_id, texto,
                       row_number() OVER (PARTITION BY pregunta_id ORDER BY texto) - 1 AS posicion
                FROM pregunta_opciones
            )
            UPDATE pregunta_opciones opcion
            SET orden = opciones_ordenadas.posicion
            FROM opciones_ordenadas
            WHERE opcion.pregunta_id = opciones_ordenadas.pregunta_id
              AND opcion.texto = opciones_ordenadas.texto;
            ALTER TABLE pregunta_opciones ALTER COLUMN orden SET NOT NULL;
        END IF;

        ALTER TABLE pregunta_opciones
            DROP CONSTRAINT IF EXISTS pregunta_respuestas_correctas_pkey;
        ALTER TABLE pregunta_opciones
            DROP CONSTRAINT IF EXISTS pregunta_opciones_pkey;
        ALTER TABLE pregunta_opciones
            ADD CONSTRAINT pregunta_opciones_pkey PRIMARY KEY (pregunta_id, orden);
    END IF;
END $$;