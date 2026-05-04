--
-- PostgreSQL database dump
--

\restrict Cz0LUt9YXzMZvTe6EDsFesNZjW82V5ff1bFczfRFwnT8uPof5oN1HYLgdYxalmr

-- Dumped from database version 18.3
-- Dumped by pg_dump version 18.1

-- Started on 2026-04-08 16:27:45

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

--
-- TOC entry 233 (class 1255 OID 16508)
-- Name: check_lesson_time(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.check_lesson_time() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
DECLARE
    lesson_start TIMESTAMP;
    lesson_end TIMESTAMP;
BEGIN
    SELECT start_time, end_time
    INTO lesson_start, lesson_end
    FROM lessons
    WHERE lesson_id = NEW.lesson_id;

    IF CURRENT_TIMESTAMP NOT BETWEEN lesson_start AND lesson_end THEN
        RAISE EXCEPTION 'Нельзя ставить оценку вне времени пары';
    END IF;

    RETURN NEW;
END;
$$;


ALTER FUNCTION public.check_lesson_time() OWNER TO postgres;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 232 (class 1259 OID 16511)
-- Name: grades; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.grades (
    grade_id integer NOT NULL,
    lesson_id integer,
    student_id integer,
    grade integer,
    attendance boolean,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT grades_grade_check CHECK (((grade >= 0) AND (grade <= 100)))
);


ALTER TABLE public.grades OWNER TO postgres;

--
-- TOC entry 231 (class 1259 OID 16510)
-- Name: grades_grade_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.grades_grade_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.grades_grade_id_seq OWNER TO postgres;

--
-- TOC entry 5095 (class 0 OID 0)
-- Dependencies: 231
-- Name: grades_grade_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.grades_grade_id_seq OWNED BY public.grades.grade_id;


--
-- TOC entry 226 (class 1259 OID 16435)
-- Name: group_students; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.group_students (
    id integer NOT NULL,
    group_id integer,
    student_id integer
);


ALTER TABLE public.group_students OWNER TO postgres;

--
-- TOC entry 225 (class 1259 OID 16434)
-- Name: group_students_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.group_students_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.group_students_id_seq OWNER TO postgres;

--
-- TOC entry 5096 (class 0 OID 0)
-- Dependencies: 225
-- Name: group_students_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.group_students_id_seq OWNED BY public.group_students.id;


--
-- TOC entry 224 (class 1259 OID 16420)
-- Name: groups; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.groups (
    group_id integer NOT NULL,
    group_name character varying(20),
    starosta_id integer
);


ALTER TABLE public.groups OWNER TO postgres;

--
-- TOC entry 223 (class 1259 OID 16419)
-- Name: groups_group_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.groups_group_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.groups_group_id_seq OWNER TO postgres;

--
-- TOC entry 5097 (class 0 OID 0)
-- Dependencies: 223
-- Name: groups_group_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.groups_group_id_seq OWNED BY public.groups.group_id;


--
-- TOC entry 228 (class 1259 OID 16453)
-- Name: lessons; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.lessons (
    lesson_id integer NOT NULL,
    teacher_id integer,
    group_id integer,
    subject character varying(100),
    start_time timestamp without time zone,
    end_time timestamp without time zone
);


ALTER TABLE public.lessons OWNER TO postgres;

--
-- TOC entry 227 (class 1259 OID 16452)
-- Name: lessons_lesson_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.lessons_lesson_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.lessons_lesson_id_seq OWNER TO postgres;

--
-- TOC entry 5098 (class 0 OID 0)
-- Dependencies: 227
-- Name: lessons_lesson_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.lessons_lesson_id_seq OWNED BY public.lessons.lesson_id;


--
-- TOC entry 220 (class 1259 OID 16390)
-- Name: roles; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.roles (
    role_id integer NOT NULL,
    role_name character varying(20)
);


ALTER TABLE public.roles OWNER TO postgres;

--
-- TOC entry 219 (class 1259 OID 16389)
-- Name: roles_role_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.roles_role_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.roles_role_id_seq OWNER TO postgres;

--
-- TOC entry 5099 (class 0 OID 0)
-- Dependencies: 219
-- Name: roles_role_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.roles_role_id_seq OWNED BY public.roles.role_id;


--
-- TOC entry 230 (class 1259 OID 16491)
-- Name: student_qr; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.student_qr (
    qr_id integer NOT NULL,
    student_id integer,
    qr_data text,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE public.student_qr OWNER TO postgres;

--
-- TOC entry 229 (class 1259 OID 16490)
-- Name: student_qr_qr_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.student_qr_qr_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.student_qr_qr_id_seq OWNER TO postgres;

--
-- TOC entry 5100 (class 0 OID 0)
-- Dependencies: 229
-- Name: student_qr_qr_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.student_qr_qr_id_seq OWNED BY public.student_qr.qr_id;


--
-- TOC entry 222 (class 1259 OID 16400)
-- Name: users; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.users (
    user_id integer NOT NULL,
    full_name character varying(100) NOT NULL,
    email character varying(100),
    password_hash text NOT NULL,
    role_id integer,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE public.users OWNER TO postgres;

--
-- TOC entry 221 (class 1259 OID 16399)
-- Name: users_user_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.users_user_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.users_user_id_seq OWNER TO postgres;

--
-- TOC entry 5101 (class 0 OID 0)
-- Dependencies: 221
-- Name: users_user_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.users_user_id_seq OWNED BY public.users.user_id;


--
-- TOC entry 4895 (class 2604 OID 16514)
-- Name: grades grade_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.grades ALTER COLUMN grade_id SET DEFAULT nextval('public.grades_grade_id_seq'::regclass);


--
-- TOC entry 4891 (class 2604 OID 16438)
-- Name: group_students id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.group_students ALTER COLUMN id SET DEFAULT nextval('public.group_students_id_seq'::regclass);


--
-- TOC entry 4890 (class 2604 OID 16423)
-- Name: groups group_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.groups ALTER COLUMN group_id SET DEFAULT nextval('public.groups_group_id_seq'::regclass);


--
-- TOC entry 4892 (class 2604 OID 16456)
-- Name: lessons lesson_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.lessons ALTER COLUMN lesson_id SET DEFAULT nextval('public.lessons_lesson_id_seq'::regclass);


--
-- TOC entry 4887 (class 2604 OID 16393)
-- Name: roles role_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.roles ALTER COLUMN role_id SET DEFAULT nextval('public.roles_role_id_seq'::regclass);


--
-- TOC entry 4893 (class 2604 OID 16494)
-- Name: student_qr qr_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.student_qr ALTER COLUMN qr_id SET DEFAULT nextval('public.student_qr_qr_id_seq'::regclass);


--
-- TOC entry 4888 (class 2604 OID 16403)
-- Name: users user_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users ALTER COLUMN user_id SET DEFAULT nextval('public.users_user_id_seq'::regclass);


--
-- TOC entry 5089 (class 0 OID 16511)
-- Dependencies: 232
-- Data for Name: grades; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.grades (grade_id, lesson_id, student_id, grade, attendance, created_at) FROM stdin;
\.


--
-- TOC entry 5083 (class 0 OID 16435)
-- Dependencies: 226
-- Data for Name: group_students; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.group_students (id, group_id, student_id) FROM stdin;
\.


--
-- TOC entry 5081 (class 0 OID 16420)
-- Dependencies: 224
-- Data for Name: groups; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.groups (group_id, group_name, starosta_id) FROM stdin;
\.


--
-- TOC entry 5085 (class 0 OID 16453)
-- Dependencies: 228
-- Data for Name: lessons; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.lessons (lesson_id, teacher_id, group_id, subject, start_time, end_time) FROM stdin;
\.


--
-- TOC entry 5077 (class 0 OID 16390)
-- Dependencies: 220
-- Data for Name: roles; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.roles (role_id, role_name) FROM stdin;
1	student
2	teacher
3	admin
\.


--
-- TOC entry 5087 (class 0 OID 16491)
-- Dependencies: 230
-- Data for Name: student_qr; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.student_qr (qr_id, student_id, qr_data, created_at) FROM stdin;
\.


--
-- TOC entry 5079 (class 0 OID 16400)
-- Dependencies: 222
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.users (user_id, full_name, email, password_hash, role_id, created_at) FROM stdin;
\.


--
-- TOC entry 5102 (class 0 OID 0)
-- Dependencies: 231
-- Name: grades_grade_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.grades_grade_id_seq', 1, false);


--
-- TOC entry 5103 (class 0 OID 0)
-- Dependencies: 225
-- Name: group_students_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.group_students_id_seq', 1, false);


--
-- TOC entry 5104 (class 0 OID 0)
-- Dependencies: 223
-- Name: groups_group_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.groups_group_id_seq', 1, false);


--
-- TOC entry 5105 (class 0 OID 0)
-- Dependencies: 227
-- Name: lessons_lesson_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.lessons_lesson_id_seq', 1, false);


--
-- TOC entry 5106 (class 0 OID 0)
-- Dependencies: 219
-- Name: roles_role_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.roles_role_id_seq', 3, true);


--
-- TOC entry 5107 (class 0 OID 0)
-- Dependencies: 229
-- Name: student_qr_qr_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.student_qr_qr_id_seq', 1, false);


--
-- TOC entry 5108 (class 0 OID 0)
-- Dependencies: 221
-- Name: users_user_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.users_user_id_seq', 1, false);


--
-- TOC entry 4919 (class 2606 OID 16519)
-- Name: grades grades_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.grades
    ADD CONSTRAINT grades_pkey PRIMARY KEY (grade_id);


--
-- TOC entry 4911 (class 2606 OID 16441)
-- Name: group_students group_students_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.group_students
    ADD CONSTRAINT group_students_pkey PRIMARY KEY (id);


--
-- TOC entry 4907 (class 2606 OID 16428)
-- Name: groups groups_group_name_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.groups
    ADD CONSTRAINT groups_group_name_key UNIQUE (group_name);


--
-- TOC entry 4909 (class 2606 OID 16426)
-- Name: groups groups_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.groups
    ADD CONSTRAINT groups_pkey PRIMARY KEY (group_id);


--
-- TOC entry 4913 (class 2606 OID 16459)
-- Name: lessons lessons_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.lessons
    ADD CONSTRAINT lessons_pkey PRIMARY KEY (lesson_id);


--
-- TOC entry 4899 (class 2606 OID 16396)
-- Name: roles roles_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.roles
    ADD CONSTRAINT roles_pkey PRIMARY KEY (role_id);


--
-- TOC entry 4901 (class 2606 OID 16398)
-- Name: roles roles_role_name_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.roles
    ADD CONSTRAINT roles_role_name_key UNIQUE (role_name);


--
-- TOC entry 4915 (class 2606 OID 16500)
-- Name: student_qr student_qr_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.student_qr
    ADD CONSTRAINT student_qr_pkey PRIMARY KEY (qr_id);


--
-- TOC entry 4917 (class 2606 OID 16502)
-- Name: student_qr student_qr_student_id_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.student_qr
    ADD CONSTRAINT student_qr_student_id_key UNIQUE (student_id);


--
-- TOC entry 4903 (class 2606 OID 16413)
-- Name: users users_email_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_email_key UNIQUE (email);


--
-- TOC entry 4905 (class 2606 OID 16411)
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (user_id);


--
-- TOC entry 4927 (class 2606 OID 16520)
-- Name: grades grades_lesson_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.grades
    ADD CONSTRAINT grades_lesson_id_fkey FOREIGN KEY (lesson_id) REFERENCES public.lessons(lesson_id);


--
-- TOC entry 4928 (class 2606 OID 16525)
-- Name: grades grades_student_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.grades
    ADD CONSTRAINT grades_student_id_fkey FOREIGN KEY (student_id) REFERENCES public.users(user_id);


--
-- TOC entry 4922 (class 2606 OID 16442)
-- Name: group_students group_students_group_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.group_students
    ADD CONSTRAINT group_students_group_id_fkey FOREIGN KEY (group_id) REFERENCES public.groups(group_id);


--
-- TOC entry 4923 (class 2606 OID 16447)
-- Name: group_students group_students_student_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.group_students
    ADD CONSTRAINT group_students_student_id_fkey FOREIGN KEY (student_id) REFERENCES public.users(user_id);


--
-- TOC entry 4921 (class 2606 OID 16429)
-- Name: groups groups_starosta_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.groups
    ADD CONSTRAINT groups_starosta_id_fkey FOREIGN KEY (starosta_id) REFERENCES public.users(user_id);


--
-- TOC entry 4924 (class 2606 OID 16465)
-- Name: lessons lessons_group_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.lessons
    ADD CONSTRAINT lessons_group_id_fkey FOREIGN KEY (group_id) REFERENCES public.groups(group_id);


--
-- TOC entry 4925 (class 2606 OID 16460)
-- Name: lessons lessons_teacher_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.lessons
    ADD CONSTRAINT lessons_teacher_id_fkey FOREIGN KEY (teacher_id) REFERENCES public.users(user_id);


--
-- TOC entry 4926 (class 2606 OID 16503)
-- Name: student_qr student_qr_student_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.student_qr
    ADD CONSTRAINT student_qr_student_id_fkey FOREIGN KEY (student_id) REFERENCES public.users(user_id);


--
-- TOC entry 4920 (class 2606 OID 16414)
-- Name: users users_role_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_role_id_fkey FOREIGN KEY (role_id) REFERENCES public.roles(role_id);


-- Completed on 2026-04-08 16:27:45

--
-- PostgreSQL database dump complete
--

\unrestrict Cz0LUt9YXzMZvTe6EDsFesNZjW82V5ff1bFczfRFwnT8uPof5oN1HYLgdYxalmr

