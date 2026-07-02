import { useEffect, useMemo, useState } from "react";
import { Link } from "react-router-dom";
import { ArrowRight, Bell, BookOpen, Calendar, Clock, Users, Sparkles } from "lucide-react";
import { format } from "date-fns";
import { es } from "date-fns/locale";

import { useAuthStore } from "@/store/auth.store";
import { axiosInstance } from "@/utils/axios";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";

export default function Home() {
	const { user } = useAuthStore();
	const [perfilTutor, setPerfilTutor] = useState(null);
	const [upcomingTutorias, setUpcomingTutorias] = useState([]);
	const [misInscripciones, setMisInscripciones] = useState([]);
	const [avisos, setAvisos] = useState([]);
	const [loading, setLoading] = useState(true);

	useEffect(() => {
		const fetchData = async () => {
			try {
				const requests = [axiosInstance.get("/tutorias")];
				if (user?.id) {
					requests.push(axiosInstance.get(`/perfil-tutor/${user.id}`));
					requests.push(axiosInstance.get("/inscripciones/mis-inscripciones"));
				}

				const [tutoriasRes, perfilRes, inscripcionesRes] = await Promise.all(requests);
				const listaTutorias = tutoriasRes.data || [];
				const tutoriasOrdenadas = [...listaTutorias].sort((a, b) => {
					const fechaA = new Date(`${a.fecha || "9999-12-31"}T${a.horaInicio || "23:59:59"}`);
					const fechaB = new Date(`${b.fecha || "9999-12-31"}T${b.horaInicio || "23:59:59"}`);
					return fechaA - fechaB;
				});

				setUpcomingTutorias(tutoriasOrdenadas.slice(0, 3));
				if (perfilRes) setPerfilTutor(perfilRes.data);
				if (inscripcionesRes) setMisInscripciones(inscripcionesRes.data || []);

				if (user?.role === "alumno") {
					const res = await axiosInstance.get("/avisos/mis-avisos");
					setAvisos(res.data?.slice(0, 3) || []);
				} else if (user?.role === "tutor" || user?.role === "admin") {
					const res = await axiosInstance.get("/avisos/mis-avisos-enviados");
					setAvisos(res.data?.slice(0, 3) || []);
				}
			} catch (error) {
				console.error("Error al cargar datos del home:", error);
			} finally {
				setLoading(false);
			}
		};
		fetchData();
	}, [user]);

	const nombreCompleto = useMemo(() => {
		if (perfilTutor?.nombreCompleto) return perfilTutor.nombreCompleto;
		return `${user?.nombre || "Usuario"} ${user?.apellido || ""}`.trim();
	}, [perfilTutor?.nombreCompleto, user?.apellido, user?.nombre]);

	if (loading) {
		return (
			<div className="flex items-center justify-center py-20">
				<div className="h-8 w-8 animate-spin rounded-full border-4 border-sky-500 border-t-transparent" />
			</div>
		);
	}

	const stats = [
		{ title: "Tutorías Disponibles", value: perfilTutor?.cantidadTutoriasActivas ?? upcomingTutorias.length, description: "Esta semana", icon: BookOpen },
		{ title: "Mis Inscripciones", value: misInscripciones.length, description: "Activas", icon: Calendar },
		{ title: "Mi Carrera", value: perfilTutor?.carrera || "—", description: perfilTutor?.titulo || "Tutor", icon: Sparkles },
		{ title: "Mi Rol", value: user?.role === "tutor" ? "Tutor" : (user?.role || "—"), description: "En la plataforma", icon: Users },
	];

	return (
		<div className="min-h-screen bg-[#A9D8FA]">
			<div className="mx-auto max-w-[1400px] px-4 py-5 sm:px-6 lg:px-8">
				<div className="overflow-hidden rounded-[36px] border border-slate-200 bg-white shadow-[0_18px_45px_rgba(15,23,42,0.10)]">
					<div className="px-4 py-5 sm:px-6 lg:px-8 lg:py-6">
						<div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
							<div>
								<h1 className="text-2xl font-bold text-slate-900 sm:text-3xl">¡Hola, {nombreCompleto || user?.nombre || "Tutor"}!</h1>
								<p className="text-sm text-slate-500">Bienvenido al Sistema de Tutorías UNNOBA</p>
							</div>
							<Button asChild className="bg-sky-600 text-white hover:bg-sky-700">
								<Link to="/dashboard/tutorias" className="flex items-center gap-2">
									Explorar Tutorías
									<ArrowRight className="h-4 w-4" />
								</Link>
							</Button>
						</div>

						<div className="mt-6 grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
							{stats.map((stat) => {
								const Icon = stat.icon;
								return (
									<Card key={stat.title} className="border-slate-200 shadow-sm">
										<CardContent className="p-5">
											<div className="flex items-start justify-between gap-4">
												<div className="space-y-2">
													<p className="text-xs font-medium uppercase tracking-wide text-slate-500">{stat.title}</p>
													{typeof stat.value === "number" ? (
														<p className="text-3xl font-bold text-slate-900">{stat.value}</p>
													) : (
														<p className="text-lg font-semibold text-slate-900">{stat.value}</p>
													)}
													<p className="text-xs text-slate-500">{stat.description}</p>
												</div>
												<div className="rounded-xl bg-sky-50 p-2 text-sky-600">
													<Icon className="h-4 w-4" />
												</div>
											</div>
										</CardContent>
									</Card>
								);
							})}
						</div>

						<div className="mt-6 grid gap-6 lg:grid-cols-[1.4fr_0.8fr]">
							<Card className="border-slate-200 shadow-sm">
								<CardHeader className="flex flex-row items-start justify-between gap-4">
									<div>
										<CardTitle className="text-xl text-slate-900">Próximas Tutorías</CardTitle>
										<CardDescription>Tutorías disponibles para inscribirte o revisar como tutor</CardDescription>
									</div>
									<Button variant="outline" size="sm" asChild>
										<Link to="/dashboard/tutorias">Ver todas</Link>
									</Button>
								</CardHeader>
								<CardContent className="space-y-3 pt-0">
									{upcomingTutorias.length > 0 ? (
										upcomingTutorias.map((tutoria) => (
											<div key={tutoria.id} className="flex flex-col gap-4 rounded-2xl border border-slate-200 bg-white p-4 shadow-sm sm:flex-row sm:items-center sm:justify-between">
												<div className="space-y-1">
													<div className="flex flex-wrap items-center gap-2">
														<h4 className="text-base font-semibold text-slate-900">{tutoria.nombre || tutoria.titulo}</h4>
														<Badge variant={tutoria.modalidad === "VIRTUAL" ? "secondary" : "outline"}>{tutoria.modalidad}</Badge>
													</div>
													<p className="text-sm text-slate-600">{tutoria.materia?.nombre || tutoria.materiaNombre || "—"}</p>
													<div className="flex flex-wrap items-center gap-4 text-xs text-slate-500">
														<span className="flex items-center gap-1"><Calendar className="h-3.5 w-3.5" />{tutoria.fecha ? format(new Date(tutoria.fecha), "d 'de' MMMM", { locale: es }) : "Sin fecha"}</span>
														<span className="flex items-center gap-1"><Clock className="h-3.5 w-3.5" />{tutoria.horaInicio?.slice(0, 5)} - {tutoria.horaFin?.slice(0, 5)}</span>
													</div>
												</div>
												<Button size="sm" asChild><Link to={`/dashboard/tutorias/${tutoria.id}`}>Ver más</Link></Button>
											</div>
										))
									) : (
										<div className="flex flex-col items-center justify-center rounded-2xl border border-dashed border-slate-300 bg-slate-50 py-12 text-center">
											<BookOpen className="mb-3 h-10 w-10 text-slate-400" />
											<p className="text-sm font-medium text-slate-700">No hay tutorías próximas</p>
											<p className="text-xs text-slate-500">Volvé más tarde o explorá las disponibles</p>
										</div>
									)}
								</CardContent>
							</Card>

							<Card className="border-slate-200 shadow-sm">
								<CardHeader className="flex flex-row items-center justify-between gap-4">
									<div className="flex items-center gap-2">
										<Bell className="h-5 w-5 text-sky-600" />
										<CardTitle className="text-xl text-slate-900">Avisos</CardTitle>
									</div>
									<Button variant="outline" size="sm" asChild>
										<Link to={user?.role === "tutor" || user?.role === "admin" ? "/dashboard/gestionar-avisos" : "/dashboard/bandeja-avisos"}>Ver todos</Link>
									</Button>
								</CardHeader>
								<CardContent className="pt-0">
									{avisos.length > 0 ? (
										<div className="space-y-4">
											{avisos.map((aviso) => (
												<div key={aviso.id} className="space-y-1 border-b border-slate-200 pb-4 last:border-0 last:pb-0">
													<h4 className="font-medium text-sm text-slate-900">{aviso.titulo}</h4>
													<p className="text-xs text-slate-500 line-clamp-2">{aviso.contenido}</p>
													<p className="text-xs text-slate-400">{format(new Date(aviso.fechaCreacion), "d MMM yyyy", { locale: es })}</p>
													<p className="text-xs text-slate-500">{aviso.nombreTutor} · {aviso.nombreTutoria}</p>
												</div>
											))}
										</div>
									) : (
										<div className="flex flex-col items-center justify-center rounded-2xl border border-dashed border-slate-300 bg-slate-50 py-12 text-center">
											<Bell className="mb-2 h-8 w-8 text-slate-400" />
											<p className="text-sm text-slate-600">No hay avisos</p>
										</div>
									)}
								</CardContent>
							</Card>
						</div>
					</div>
				</div>
			</div>
		</div>
	);
}