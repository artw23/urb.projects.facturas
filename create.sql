create table factura (id uuid not null, created_at timestamp not null, cantidad_final float8, cantidad_inicial float8, clave_catastral varchar(255), condominio varchar(255), fecha date, nombre_factura varchar(255), numero varchar(255), operacion varchar(255), pdf_file_id uuid, pdf_url varchar(255), periodo varchar(255), receipt_file_id uuid, reporte_id uuid, xml_url varchar(255), xml_file_id uuid, primary key (id))
create table factura_errores (factura_id uuid not null, errores varchar(255))
create table file (id uuid not null, created_at timestamp not null, content oid, extension varchar(255), nombre varchar(255), primary key (id))
create table report (id uuid not null, created_at timestamp not null, input_file_id uuid, invoice_type int4, output_file_id uuid, payment_date date, status int4, primary key (id))
alter table if exists factura_errores add constraint FKc99o7xcv0689floev5jpb7syt foreign key (factura_id) references factura
create table factura (id uuid not null, created_at timestamp not null, cantidad_final float8, cantidad_inicial float8, clave_catastral varchar(255), condominio varchar(255), fecha date, nombre_factura varchar(255), numero varchar(255), operacion varchar(255), pdf_file_id uuid, pdf_url varchar(255), periodo varchar(255), receipt_file_id uuid, reporte_id uuid, xml_url varchar(255), xml_file_id uuid, primary key (id))
create table factura_errores (factura_id uuid not null, errores varchar(255))
create table file (id uuid not null, created_at timestamp not null, content oid, extension varchar(255), nombre varchar(255), primary key (id))
create table report (id uuid not null, created_at timestamp not null, input_file_id uuid, invoice_type int4, output_file_id uuid, payment_date date, status int4, primary key (id))
alter table if exists factura_errores add constraint FKc99o7xcv0689floev5jpb7syt foreign key (factura_id) references factura
create table factura (id uuid not null, created_at timestamp not null, cantidad_final float8, cantidad_inicial float8, clave_catastral varchar(255), condominio varchar(255), fecha date, nombre_factura varchar(255), numero varchar(255), operacion varchar(255), pdf_file_id uuid, pdf_url varchar(255), periodo varchar(255), receipt_file_id uuid, reporte_id uuid, xml_url varchar(255), xml_file_id uuid, primary key (id))
create table factura_errores (factura_id uuid not null, errores varchar(255))
create table file (id uuid not null, created_at timestamp not null, content oid, extension varchar(255), nombre varchar(255), primary key (id))
create table report (id uuid not null, created_at timestamp not null, input_file_id uuid, invoice_type int4, output_file_id uuid, payment_date date, status int4, primary key (id))
alter table if exists factura_errores add constraint FKc99o7xcv0689floev5jpb7syt foreign key (factura_id) references factura
create table factura_errores (factura_id uuid not null, errores varchar(255))
create table files (id uuid not null, created_at timestamp not null, content oid, nombre varchar(255), primary key (id))
create table invoices (id uuid not null, created_at timestamp not null, cantidad_final float8, cantidad_inicial float8, clave_catastral varchar(255), condominio varchar(255), fecha date, nombre_factura varchar(255), numero varchar(255), operacion varchar(255), pdf_file_id uuid, pdf_url varchar(255), periodo varchar(255), receipt_file_id uuid, reporte_id uuid, xml_url varchar(255), xml_file_id uuid, primary key (id))
create table report (id uuid not null, created_at timestamp not null, input_file_id uuid, invoice_type int4, output_file_id uuid, payment_date date, status int4, primary key (id))
alter table if exists factura_errores add constraint FKhp06265mnggp3t81ybpv0pf9a foreign key (factura_id) references invoices
create table factura (id uuid not null, created_at timestamp not null, cantidad_final float8, cantidad_inicial float8, clave_catastral varchar(255), condominio varchar(255), fecha date, nombre_factura varchar(255), numero varchar(255), operacion varchar(255), pdf_file_id uuid, pdf_url varchar(255), periodo varchar(255), receipt_file_id uuid, reporte_id uuid, xml_url varchar(255), xml_file_id uuid, primary key (id))
create table factura_errores (factura_id uuid not null, errores varchar(255))
create table file (id uuid not null, created_at timestamp not null, content oid, nombre varchar(255), primary key (id))
create table report (id uuid not null, created_at timestamp not null, input_file_id uuid, invoice_type int4, output_file_id uuid, payment_date date, status int4, primary key (id))
alter table if exists factura_errores add constraint FKc99o7xcv0689floev5jpb7syt foreign key (factura_id) references factura
create table factura (id uuid not null, created_at timestamp not null, cantidad_final float8, cantidad_inicial float8, clave_catastral varchar(255), condominio varchar(255), fecha date, nombre_factura varchar(255), numero varchar(255), operacion varchar(255), pdf_file_id uuid, pdf_url varchar(255), periodo varchar(255), receipt_file_id uuid, reporte_id uuid, xml_url varchar(255), xml_file_id uuid, primary key (id))
create table factura_errores (factura_id uuid not null, errores varchar(255))
create table file (id uuid not null, created_at timestamp not null, content oid, nombre varchar(255), primary key (id))
create table report (id uuid not null, created_at timestamp not null, input_file_id uuid, invoice_type int4, output_file_id uuid, payment_date date, status int4, primary key (id))
alter table if exists factura_errores add constraint FKc99o7xcv0689floev5jpb7syt foreign key (factura_id) references factura
create table factura (id uuid not null, created_at timestamp not null, cantidad_final float8, cantidad_inicial float8, clave_catastral varchar(255), condominio varchar(255), fecha date, nombre_factura varchar(255), numero varchar(255), operacion varchar(255), pdf_file_id uuid, pdf_url varchar(255), periodo varchar(255), receipt_file_id uuid, reporte_id uuid, xml_url varchar(255), xml_file_id uuid, primary key (id))
create table factura_errores (factura_id uuid not null, errores varchar(255))
create table file (id uuid not null, created_at timestamp not null, content oid, nombre varchar(255), primary key (id))
create table report (id uuid not null, created_at timestamp not null, input_file_id uuid, invoice_type int4, output_file_id uuid, payment_date date, status int4, primary key (id))
alter table if exists factura_errores add constraint FKc99o7xcv0689floev5jpb7syt foreign key (factura_id) references factura
create table factura (id uuid not null, created_at timestamp not null, cantidad_final float8, cantidad_inicial float8, clave_catastral varchar(255), condominio varchar(255), fecha date, nombre_factura varchar(255), numero varchar(255), operacion varchar(255), pdf_file_id uuid, pdf_url varchar(255), periodo varchar(255), receipt_file_id uuid, reporte_id uuid, xml_url varchar(255), xml_file_id uuid, primary key (id))
create table factura_errores (factura_id uuid not null, errores varchar(255))
create table file (id uuid not null, created_at timestamp not null, content oid, nombre varchar(255), primary key (id))
create table report (id uuid not null, created_at timestamp not null, input_file_id uuid, invoice_type int4, output_file_id uuid, payment_date date, status int4, primary key (id))
alter table if exists factura_errores add constraint FKc99o7xcv0689floev5jpb7syt foreign key (factura_id) references factura
create table factura (id uuid not null, created_at timestamp not null, cantidad_final float8, cantidad_inicial float8, clave_catastral varchar(255), condominio varchar(255), fecha date, nombre_factura varchar(255), numero varchar(255), operacion varchar(255), pdf_file_id uuid, pdf_url varchar(255), periodo varchar(255), receipt_file_id uuid, reporte_id uuid, xml_url varchar(255), xml_file_id uuid, primary key (id))
create table factura_errores (factura_id uuid not null, errores varchar(255))
create table file (id uuid not null, created_at timestamp not null, content bytea, nombre varchar(255), primary key (id))
create table report (id uuid not null, created_at timestamp not null, input_file_id uuid, invoice_type int4, output_file_id uuid, payment_date date, status int4, primary key (id))
alter table if exists factura_errores add constraint FKc99o7xcv0689floev5jpb7syt foreign key (factura_id) references factura
create table factura (id uuid not null, created_at timestamp not null, cantidad_final float8, cantidad_inicial float8, clave_catastral varchar(255), condominio varchar(255), fecha date, nombre_factura varchar(255), numero varchar(255), operacion varchar(255), pdf_file_id uuid, pdf_url varchar(255), periodo varchar(255), receipt_file_id uuid, reporte_id uuid, xml_url varchar(255), xml_file_id uuid, primary key (id))
create table factura_errores (factura_id uuid not null, errores varchar(255))
create table file (id uuid not null, created_at timestamp not null, content bytea, nombre varchar(255), primary key (id))
create table report (id uuid not null, created_at timestamp not null, input_file_id uuid, invoice_type int4, output_file_id uuid, payment_date date, status int4, primary key (id))
alter table if exists factura_errores add constraint FKc99o7xcv0689floev5jpb7syt foreign key (factura_id) references factura
create table factura (id uuid not null, created_at timestamp not null, cantidad_final float8, cantidad_inicial float8, clave_catastral varchar(255), condominio varchar(255), fecha date, nombre_factura varchar(255), numero varchar(255), operacion varchar(255), pdf_file_id uuid, pdf_url varchar(255), periodo varchar(255), receipt_file_id uuid, reporte_id uuid, xml_url varchar(255), xml_file_id uuid, primary key (id))
create table factura_errores (factura_id uuid not null, errores varchar(255))
create table file (id uuid not null, created_at timestamp not null, content bytea, nombre varchar(255), primary key (id))
create table report (id uuid not null, created_at timestamp not null, input_file_id uuid, invoice_type int4, output_file_id uuid, payment_date date, status int4, primary key (id))
alter table if exists factura_errores add constraint FKc99o7xcv0689floev5jpb7syt foreign key (factura_id) references factura
create table factura (id uuid not null, created_at timestamp not null, cantidad_final float8, cantidad_inicial float8, clave_catastral varchar(255), condominio varchar(255), fecha date, nombre_factura varchar(255), numero varchar(255), operacion varchar(255), pdf_file_id uuid, pdf_url varchar(255), periodo varchar(255), receipt_file_id uuid, reporte_id uuid, xml_url varchar(255), xml_file_id uuid, primary key (id))
create table factura_errores (factura_id uuid not null, errores varchar(255))
create table file (id uuid not null, created_at timestamp not null, content bytea, nombre varchar(255), primary key (id))
create table report (id uuid not null, created_at timestamp not null, input_file_id uuid, invoice_type int4, output_file_id uuid, payment_date date, status int4, primary key (id))
alter table if exists factura_errores add constraint FKc99o7xcv0689floev5jpb7syt foreign key (factura_id) references factura
create table factura (id uuid not null, created_at timestamp not null, cantidad_final float8, cantidad_inicial float8, clave_catastral varchar(255), condominio varchar(255), fecha date, nombre_factura varchar(255), numero varchar(255), operacion varchar(255), pdf_file_id uuid, pdf_url varchar(255), periodo varchar(255), receipt_file_id uuid, reporte_id uuid, xml_url varchar(255), xml_file_id uuid, primary key (id))
create table factura_errores (factura_id uuid not null, errores varchar(255))
create table file (id uuid not null, created_at timestamp not null, content bytea, nombre varchar(255), primary key (id))
create table report (id uuid not null, created_at timestamp not null, input_file_id uuid, invoice_type int4, output_file_id uuid, payment_date date, status int4, primary key (id))
alter table if exists factura_errores add constraint FKc99o7xcv0689floev5jpb7syt foreign key (factura_id) references factura
create table factura (id uuid not null, created_at timestamp not null, cantidad_final float8, cantidad_inicial float8, clave_catastral varchar(255), condominio varchar(255), fecha date, nombre_factura varchar(255), numero varchar(255), operacion varchar(255), pdf_file_id uuid, pdf_url varchar(255), periodo varchar(255), receipt_file_id uuid, reporte_id uuid, xml_url varchar(255), xml_file_id uuid, primary key (id))
create table factura_errores (factura_id uuid not null, errores varchar(255))
create table file (id uuid not null, created_at timestamp not null, content bytea, nombre varchar(255), primary key (id))
create table report (id uuid not null, created_at timestamp not null, input_file_id uuid, invoice_type int4, output_file_id uuid, payment_date date, status int4, primary key (id))
alter table if exists factura_errores add constraint FKc99o7xcv0689floev5jpb7syt foreign key (factura_id) references factura
