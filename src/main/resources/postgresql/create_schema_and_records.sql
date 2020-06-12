DROP TABLE public.item;
DROP TABLE public.project;

CREATE TABLE public.project
(
    id bigint NOT NULL,
    name character varying(20) COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT project_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;
ALTER TABLE public.project
    OWNER to bf;

INSERT INTO public.project VALUES (nextval('hibernate_sequence'), 'Test Projekt');
INSERT INTO public.project VALUES (nextval('hibernate_sequence'), 'Test Projekt2');    
    
CREATE TABLE public.item
(
    id bigint NOT NULL,
    imageurl character varying(255) COLLATE pg_catalog."default",
    level integer NOT NULL,
    name character varying(20) COLLATE pg_catalog."default" NOT NULL,
    item_id bigint,
    project_id bigint,
    CONSTRAINT item_pkey PRIMARY KEY (id),
    CONSTRAINT fk62g7oly5161ig38aq3gwk19mx FOREIGN KEY (item_id)
        REFERENCES public.item (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fkafqmt8ghu1357ckg6v4t14vto FOREIGN KEY (project_id)
        REFERENCES public.project (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

TABLESPACE pg_default;
ALTER TABLE public.item
    OWNER to bf;
    

INSERT INTO public.item VALUES (nextval('hibernate_sequence'), '/main.jpg', 0, 'main', null,1);
INSERT INTO public.item VALUES (nextval('hibernate_sequence'), '/sub.jpg', 1, 'sub1', 3,1);
INSERT INTO public.item VALUES (nextval('hibernate_sequence'), '/sub.jpg', 1, 'sub2', 3,1);
INSERT INTO public.item VALUES (nextval('hibernate_sequence'), '/sub.jpg', 2, 'sub1sub1', 4,1);