--
-- PostgreSQL database dump
--

-- Dumped from database version 17.0 (Debian 17.0-1.pgdg120+1)
-- Dumped by pg_dump version 17.0 (Debian 17.0-1.pgdg120+1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: applications; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.applications (
    active boolean NOT NULL,
    amount numeric(38,2) NOT NULL,
    term integer NOT NULL,
    created_date timestamp(6) with time zone NOT NULL,
    updated_date timestamp(6) with time zone NOT NULL,
    client_id uuid NOT NULL,
    id uuid NOT NULL,
    application_status character varying(255) NOT NULL,
    purpose character varying(255) NOT NULL,
    CONSTRAINT applications_application_status_check CHECK (((application_status)::text = ANY ((ARRAY['REVIEWING'::character varying, 'APPROVED'::character varying, 'REJECTED'::character varying])::text[]))),
    CONSTRAINT applications_purpose_check CHECK (((purpose)::text = ANY ((ARRAY['CONSUMER'::character varying, 'MORTGAGE'::character varying, 'AUTO'::character varying, 'BUSINESS'::character varying, 'EDUCATION'::character varying, 'MEDICAL'::character varying, 'REFINANCE'::character varying, 'SECURED'::character varying, 'UNSECURED'::character varying, 'OVERDRAFT'::character varying])::text[])))
);


ALTER TABLE public.applications OWNER TO postgres;

--
-- Name: clients; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.clients (
    active boolean NOT NULL,
    annual_income numeric(38,2),
    birth_date date NOT NULL,
    total_monthly_debt_payment numeric(38,2),
    created_date timestamp(6) with time zone NOT NULL,
    updated_date timestamp(6) with time zone NOT NULL,
    id uuid NOT NULL,
    cif character varying(30) NOT NULL,
    email character varying(255) NOT NULL,
    employment_status character varying(255) NOT NULL,
    full_name character varying(255) NOT NULL,
    CONSTRAINT clients_employment_status_check CHECK (((employment_status)::text = ANY ((ARRAY['EMPLOYED'::character varying, 'SELF_EMPLOYED'::character varying, 'UNEMPLOYED'::character varying])::text[])))
);


ALTER TABLE public.clients OWNER TO postgres;

--
-- Name: offers; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.offers (
    active boolean NOT NULL,
    approved_amount numeric(38,2) NOT NULL,
    apr numeric(38,2) NOT NULL,
    monthly_payment numeric(38,2) NOT NULL,
    term_months integer NOT NULL,
    expires_at timestamp(6) with time zone NOT NULL,
    application_id uuid NOT NULL,
    id uuid NOT NULL,
    product_id uuid NOT NULL,
    status character varying(255) NOT NULL,
    CONSTRAINT offers_status_check CHECK (((status)::text = ANY ((ARRAY['PROPOSED'::character varying, 'ACCEPTED'::character varying, 'REJECTED'::character varying, 'EXPIRED'::character varying, 'CANCELED'::character varying])::text[])))
);


ALTER TABLE public.offers OWNER TO postgres;

--
-- Name: payments; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.payments (
    active boolean NOT NULL,
    amount numeric(38,2) NOT NULL,
    due_date timestamp(6) with time zone NOT NULL,
    processed_at timestamp(6) with time zone,
    client_id uuid NOT NULL,
    id uuid NOT NULL,
    offer_id uuid NOT NULL,
    reference character varying(255),
    status character varying(255) NOT NULL,
    CONSTRAINT payments_status_check CHECK (((status)::text = ANY ((ARRAY['PLANNED'::character varying, 'COMPLETED'::character varying, 'FAILED'::character varying, 'DELAYED'::character varying, 'CANCELED'::character varying])::text[])))
);


ALTER TABLE public.payments OWNER TO postgres;

--
-- Name: products; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.products (
    active boolean NOT NULL,
    base_apr_max numeric(38,2) NOT NULL,
    base_apr_min numeric(38,2) NOT NULL,
    max_amount numeric(38,2) NOT NULL,
    max_term_months integer NOT NULL,
    min_amount numeric(38,2) NOT NULL,
    min_term_months integer NOT NULL,
    version integer NOT NULL,
    created_at timestamp(6) with time zone NOT NULL,
    updated_at timestamp(6) with time zone NOT NULL,
    id uuid NOT NULL,
    code character varying(255) NOT NULL,
    description character varying(255),
    name character varying(255) NOT NULL,
    purpose character varying(255) NOT NULL,
    CONSTRAINT products_purpose_check CHECK (((purpose)::text = ANY ((ARRAY['CONSUMER'::character varying, 'MORTGAGE'::character varying, 'AUTO'::character varying, 'BUSINESS'::character varying, 'EDUCATION'::character varying, 'MEDICAL'::character varying, 'REFINANCE'::character varying, 'SECURED'::character varying, 'UNSECURED'::character varying, 'OVERDRAFT'::character varying])::text[])))
);


ALTER TABLE public.products OWNER TO postgres;

--
-- Data for Name: applications; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.applications (active, amount, term, created_date, updated_date, client_id, id, application_status, purpose) FROM stdin;
t	17435.05	7	2025-10-22 21:45:12.231178+00	2025-10-22 21:52:35.60655+00	058aaf1a-832e-4390-87f0-4ba1d0e3d3d0	e5f05a03-da4f-4499-9f78-0aadcbfd1051	REVIEWING	CONSUMER
t	300000.00	24	2025-10-21 17:06:47.384133+00	2025-10-21 17:06:47.384133+00	11111111-2222-3333-4444-555555555555	aaaaaaa1-bbbb-cccc-dddd-eeeeeeeeeee1	REVIEWING	CONSUMER
t	300000.00	24	2025-10-21 17:06:47.384133+00	2025-10-21 17:06:47.384133+00	11111111-2222-3333-4444-555555555557	aaaaaaa1-bbbb-cccc-dddd-eeeeeeeeeee3	REVIEWING	CONSUMER
t	300000.00	24	2025-10-21 17:06:47.384133+00	2025-10-21 17:06:47.384133+00	11111111-2222-3333-4444-555555555556	aaaaaaa1-bbbb-cccc-dddd-eeeeeeeeeee2	REVIEWING	CONSUMER
t	26033.87	38	2025-10-22 21:58:17.729381+00	2025-10-22 21:58:17.729381+00	058aaf1a-832e-4390-87f0-4ba1d0e3d3d0	17be986b-d173-480d-a9bf-fbc3aa683368	REVIEWING	REFINANCE
t	7592.85	12	2025-10-25 14:28:23.952973+00	2025-10-25 14:28:23.952973+00	11111111-2222-3333-4444-555555555555	5ba8f4c3-3f13-4730-8b40-1c2adda781f4	REVIEWING	MORTGAGE
t	47318.47	28	2025-10-25 14:20:12.617362+00	2025-10-25 14:20:12.617362+00	11111111-2222-3333-4444-555555555555	03933db4-e83c-48b4-8bb2-67dfb0beba35	REVIEWING	REFINANCE
t	47044.39	42	2025-10-25 14:17:41.341777+00	2025-10-25 14:17:41.341777+00	11111111-2222-3333-4444-555555555555	609d3a68-fc12-41c5-957e-43fdaaaa257a	REVIEWING	SECURED
t	16851.38	58	2025-10-25 14:50:03.347288+00	2025-11-05 18:26:30.199554+00	11111111-2222-3333-4444-555555555555	29289d28-a861-4453-aa3d-c21a8579f122	REVIEWING	CONSUMER
t	37108.39	57	2025-10-25 14:49:15.740908+00	2025-12-15 15:22:15.387471+00	11111111-2222-3333-4444-555555555555	8f1db598-2942-4272-baaf-bf56ee6fd4fa	REVIEWING	CONSUMER
\.


--
-- Data for Name: clients; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.clients (active, annual_income, birth_date, total_monthly_debt_payment, created_date, updated_date, id, cif, email, employment_status, full_name) FROM stdin;
t	1200000.00	1989-03-15	15000.00	2025-10-21 17:06:47.315978+00	2025-10-21 17:06:47.315978+00	11111111-2222-3333-4444-555555555557	CIF1234567892	ivan.ptrov@example.com	EMPLOYED	Ivan Petrov
t	100000.00	2000-01-01	1000.00	2025-10-21 22:05:55.03583+00	2025-10-21 22:05:55.03583+00	95dca5dc-4eac-4688-8296-4a33aa76255f	CIF973322	user6992@example.com	EMPLOYED	Ivanov Ivan Ivanovich
t	100000.00	2000-01-01	1000.00	2025-10-22 00:14:55.082698+00	2025-10-22 00:14:55.082698+00	bb0b354f-d930-4ad0-a939-bbca7bfdf680	CIF225014	user314@example.com	EMPLOYED	Ivanov Ivan Ivanovich
t	100000.00	2000-01-01	1000.00	2025-10-22 00:15:36.494095+00	2025-10-22 00:15:36.494095+00	3fe39de7-80ef-4554-a83f-9741160af769	CIF627471	user8815@example.com	EMPLOYED	Ivanov Ivan Ivanovich
t	100000.00	2000-01-01	1000.00	2025-10-22 21:05:41.891589+00	2025-10-22 21:05:41.891589+00	a2b73d45-03e3-42aa-a56d-281d2a5cadc4	CIF182705	user2044@example.com	EMPLOYED	Ivanov Ivan Ivanovich
t	100000.00	2000-01-01	1000.00	2025-10-22 21:05:51.723952+00	2025-10-22 21:05:51.723952+00	87ec1460-9b52-49c2-b6dc-252e397b3788	CIF749527	user9543@example.com	EMPLOYED	Ivanov Ivan Ivanovich
t	100000.00	2000-01-01	1000.00	2025-10-22 21:06:15.845318+00	2025-10-22 21:06:15.845318+00	9ae6605b-352d-4ffb-803f-a3796eb92cc4	CIF791785	user2713@example.com	EMPLOYED	Ivanov Ivan Ivanovich
t	100000.00	2000-01-01	1000.00	2025-10-22 21:06:20.484804+00	2025-10-22 21:06:20.484804+00	75ccd7b9-fa66-4199-9496-594f929380f8	CIF437086	user4459@example.com	EMPLOYED	Ivanov Ivan Ivanovich
t	100000.00	2000-01-01	1000.00	2025-10-22 21:06:23.05553+00	2025-10-22 21:06:23.05553+00	1cfe87ad-1b5d-4d17-8bf9-e6459a6559f0	CIF489104	user8247@example.com	EMPLOYED	Ivanov Ivan Ivanovich
t	198924.00	1982-10-06	3813.00	2025-10-22 21:23:41.401914+00	2025-10-22 21:23:41.401914+00	e5e389f0-546d-4dba-8821-e7e45b9d917f	CIF-1761168221342-61	alexey.sidorov331@example.com	UNEMPLOYED	Sidorov Alexey Sergeevich
t	62004.85	1986-11-30	719.48	2025-10-22 21:34:23.660841+00	2025-10-22 21:34:23.660841+00	65fea094-ae5f-4e80-8983-ce0deefc4eeb	CIF-1761168863615-60	client_1761168863615_7fb6318f@example.com	EMPLOYED	Petrov Ivan Petrovich
f	261880.01	1970-10-31	367.76	2025-10-22 21:34:30.315961+00	2025-10-22 21:37:57.391568+00	be5dcfd9-1aaf-4ba3-b74e-5f1c0aaa6527	CIF-1761168870265-24	client_1761168973358_10f3a20a@example.com	EMPLOYED	Petrov Sergey Dmitrievich
t	177019.02	1991-02-15	1292.80	2025-10-22 21:38:15.551549+00	2025-10-22 21:38:18.948282+00	058aaf1a-832e-4390-87f0-4ba1d0e3d3d0	CIF-1761169095517-86	client_1761169095518_a4719db4@example.com	SELF_EMPLOYED	Kuznetsov Nikolay Nikolaevich
t	113545.60	1974-09-06	598.33	2025-10-22 22:01:54.96156+00	2025-10-22 22:01:54.96156+00	38495b13-e622-4f9b-a39c-97c1fe8c66a0	CIF-1761170514883-68	client_1761170514883_9cab27f1@example.com	SELF_EMPLOYED	Petrov Dmitry Nikolaevich
t	104788.29	1989-05-31	1162.04	2025-10-22 22:05:55.301203+00	2025-10-22 22:05:55.301203+00	20292665-5654-4a9f-94be-c9cc3d1dec1d	CIF-1761170755230-63	client_1761170755230_fbbadfce@example.com	UNEMPLOYED	Ivanov Petr Ivanovich
t	76972.39	2003-07-29	217.78	2025-10-22 22:17:15.724829+00	2025-10-22 22:17:15.724829+00	3dba31a8-23ad-4a12-a88d-8089f90a9377	CIF-1761170762666-83	client_1761170762667_9dfa727c@example.com	SELF_EMPLOYED	Smirnov Nikolay Sergeevich
t	179597.34	1982-08-16	4201.82	2025-10-22 22:17:27.041597+00	2025-10-22 22:24:14.797581+00	07b9ef36-9413-45d3-92d2-f58f58f6bfd8	CIF-1761171447015-47	client_1761171854765_5ca66ea9@example.com	SELF_EMPLOYED	Smirnov Ivan Ivanovich
f	246518.22	1975-11-24	3714.57	2025-10-22 22:25:27.47806+00	2025-10-22 22:26:03.645109+00	9cd5706f-b1bf-489c-b242-e805258afb11	CIF-1761171927439-5	client_1761171950603_6ddff06e@example.com	SELF_EMPLOYED	Smirnov Sergey Dmitrievich
t	1200000.00	1989-03-15	150000.00	2025-10-21 17:06:47.315978+00	2025-10-21 17:06:47.315978+00	11111111-2222-3333-4444-555555555556	CIF1234567891	ivan.petov@example.com	EMPLOYED	Ivan Petrov
t	175278.08	1993-09-28	4591.52	2025-10-29 22:28:07.241852+00	2025-10-29 22:28:07.241852+00	c084296a-cf13-46dc-b518-90b3f39a6bf3	CIF-1761776887046-3	client-1761776887046@example.com	EMPLOYED	Sidorov Sergey Ivanovich
t	197486.26	1998-06-13	630.52	2025-10-29 23:30:32.032862+00	2025-10-29 23:30:32.032862+00	df8b5eda-ea8d-4504-a729-72306d1e0aa5	CIF-1761780632001-7	client-1761780632001@example.com	UNEMPLOYED	Kuznetsov Ivan Petrovich
t	850000.00	1990-06-12	15000.00	2025-10-30 12:18:01.932041+00	2025-10-30 12:18:01.932041+00	2d497999-4fed-44f2-a78a-082d78c9770d	CIF987654	anna.smirnova@example.com	EMPLOYED	Анна Смирнова
t	1200000.00	1989-03-15	10000.00	2025-10-21 17:06:47.315978+00	2025-10-21 17:06:47.315978+00	11111111-2222-3333-4444-555555555555	CIF1234567890	ivan.petrov@example.com	EMPLOYED	Ivan Petrov
\.


--
-- Data for Name: offers; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.offers (active, approved_amount, apr, monthly_payment, term_months, expires_at, application_id, id, product_id, status) FROM stdin;
t	47265.40	6.91	975.10	57	2025-12-23 13:42:28.79573+00	8f1db598-2942-4272-baaf-bf56ee6fd4fa	6636acb6-e49f-464f-ab64-067c017aabf9	550e8400-e29b-41d4-a716-446655440000	CANCELED
t	26266.61	6.91	1131.12	25	2025-12-23 13:56:10.846021+00	8f1db598-2942-4272-baaf-bf56ee6fd4fa	86576665-cf6f-40fe-99e8-f8384b980511	550e8400-e29b-41d4-a716-446655440000	CANCELED
t	15030.08	5.53	921.24	17	2025-12-23 13:56:33.826049+00	8f1db598-2942-4272-baaf-bf56ee6fd4fa	97c2d29c-ea55-4d3a-84df-f12bb71ad896	776e5c86-76c1-4b35-a94a-1c245784976c	CANCELED
t	46632.77	6.91	1936.33	26	2025-12-23 13:56:37.71098+00	8f1db598-2942-4272-baaf-bf56ee6fd4fa	56fdff49-2abb-47c3-b40c-465d84393e5f	550e8400-e29b-41d4-a716-446655440000	CANCELED
t	14237.81	5.53	562.03	27	2025-12-23 13:56:42.744826+00	8f1db598-2942-4272-baaf-bf56ee6fd4fa	73af55b8-90e3-4a6f-92c4-b1473ff531d0	776e5c86-76c1-4b35-a94a-1c245784976c	CANCELED
t	37108.39	6.91	765.56	57	2025-12-23 15:22:18.301942+00	8f1db598-2942-4272-baaf-bf56ee6fd4fa	6b88969a-c727-4bcb-a3ce-037b7142dd3d	550e8400-e29b-41d4-a716-446655440000	PROPOSED
\.


--
-- Data for Name: payments; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.payments (active, amount, due_date, processed_at, client_id, id, offer_id, reference, status) FROM stdin;
\.


--
-- Data for Name: products; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.products (active, base_apr_max, base_apr_min, max_amount, max_term_months, min_amount, min_term_months, version, created_at, updated_at, id, code, description, name, purpose) FROM stdin;
t	15.00	5.00	50000.00	60	5000.00	6	1	2025-10-20 10:00:00+00	2025-10-20 10:00:00+00	550e8400-e29b-41d4-a716-446655440000	CONSUMER_STD	Basic consumer credit product for personal expenses and general needs.	Standard Consumer Loan	CONSUMER
f	10.00	5.00	16827.00	29	6597.00	7	3	2025-10-22 22:35:38.908413+00	2025-10-22 22:40:09.876475+00	8bf96769-0807-4809-b128-ea53f36d2869	PRD-UNSECURED-1761172538828-46	Updated at 1761172775883	Product Updated 1761172775883	UNSECURED
t	15.00	5.00	50000.00	60	5000.00	6	1	2025-10-23 13:36:32.676707+00	2025-10-23 13:36:32.676707+00	487e7d82-9d45-483c-8b66-42199ef3fed0	DEMO_CONSUMER-10	Demo product for GraphiQL run	Demo Consumer Loan	CONSUMER
t	27.00	9.00	50000.00	60	15000.00	6	1	2025-10-21 08:21:40.78999+00	2025-10-21 08:21:40.78999+00	e47baead-9250-423c-876a-2349b19b1c2b	DEMO_CONSUMER-9	Demo product for GraphiQL run	Demo Consumer Loan	CONSUMER
t	12.00	4.00	20000.00	60	500.00	6	1	2025-10-20 20:23:07.556042+00	2025-10-20 20:23:07.556042+00	776e5c86-76c1-4b35-a94a-1c245784976c	DEMO_CONSUMER-8	Demo product for GraphiQL run	Demo Consumer Loan	CONSUMER
\.


--
-- Name: applications applications_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.applications
    ADD CONSTRAINT applications_pkey PRIMARY KEY (id);


--
-- Name: clients clients_cif_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.clients
    ADD CONSTRAINT clients_cif_key UNIQUE (cif);


--
-- Name: clients clients_email_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.clients
    ADD CONSTRAINT clients_email_key UNIQUE (email);


--
-- Name: clients clients_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.clients
    ADD CONSTRAINT clients_pkey PRIMARY KEY (id);


--
-- Name: offers offers_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.offers
    ADD CONSTRAINT offers_pkey PRIMARY KEY (id);


--
-- Name: payments payments_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.payments
    ADD CONSTRAINT payments_pkey PRIMARY KEY (id);


--
-- Name: payments payments_reference_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.payments
    ADD CONSTRAINT payments_reference_key UNIQUE (reference);


--
-- Name: products products_code_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.products
    ADD CONSTRAINT products_code_key UNIQUE (code);


--
-- Name: products products_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.products
    ADD CONSTRAINT products_pkey PRIMARY KEY (id);


--
-- Name: payments fk7q4i5uacsdt9cx0xx77nwmaso; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.payments
    ADD CONSTRAINT fk7q4i5uacsdt9cx0xx77nwmaso FOREIGN KEY (client_id) REFERENCES public.clients(id);


--
-- Name: payments fkdjfvy8gnj8b2t7j84cnc39vih; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.payments
    ADD CONSTRAINT fkdjfvy8gnj8b2t7j84cnc39vih FOREIGN KEY (offer_id) REFERENCES public.offers(id);


--
-- Name: applications fkf0heeugoetqoqkdxmxl06ly6g; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.applications
    ADD CONSTRAINT fkf0heeugoetqoqkdxmxl06ly6g FOREIGN KEY (client_id) REFERENCES public.clients(id);


--
-- Name: offers fkhj817lidvjyh8tdemki8j4ps1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.offers
    ADD CONSTRAINT fkhj817lidvjyh8tdemki8j4ps1 FOREIGN KEY (application_id) REFERENCES public.applications(id);


--
-- Name: offers fkjf1jh3h4v4m7diel8vvhmuqas; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.offers
    ADD CONSTRAINT fkjf1jh3h4v4m7diel8vvhmuqas FOREIGN KEY (product_id) REFERENCES public.products(id);


--
-- PostgreSQL database dump complete
--

