import { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import { useAuthStore } from "@/store/auth.store";
import { axiosInstance } from "@/utils/axios";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { BookOpen, Calendar, Clock, Users, ArrowRight, Bell, TrendingUp, ChevronDown, ChevronUp } from "lucide-react";
import { format } from "date-fns";
import { es } from "date-fns/locale";

export default function Home() {
	const { user } = useAuthStore();
	const [upcomingTutorias, setUpcomingTutorias] = useState([]);
	const [avisos, setAvisos] = useState([]);
	const [loading, setLoading] = useState(true);
	const [expandedTutoriaId, setExpandedTutoriaId] = useState(null);

	useEffect(() => {
		const fetchData = async () => {
			try {
				const [tutoriasRes] = await Promise.all([
					axiosInstance.get("/tutorias"),
				]);
				setUpcomingTutorias(tutoriasRes.data?.slice(0, 3) || []);

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

	const toggleTutoriaDetails = (tutoriaId) => {
		setExpandedTutoriaId((currentId) => (currentId === tutoriaId ? null : tutoriaId));
	};

	if (loading) {
		return (
			<div className="flex items-center justify-center py-20">
				<div className="h-8 w-8 animate-spin rounded-full border-4 border-sky-500 border-t-transparent" />
			</div>
		);
	}

	return (
		<div>
			<div className="container mx-auto space-y-6 p-6">
			{/* Welcome Section */}
			<div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
				<div>
					<h1 className="text-2xl font-bold text-foreground">
						¡Hola, {user?.nombre}!
					</h1>
					<p className="text-muted-foreground">
						Bienvenido al Sistema de Tutorías UNNOBA
					</p>
				</div>
				<Button asChild
                className=" bg-sky-500 hover:bg-sky-600 text-white">
					<Link to="/dashboard/tutorias" className="flex items-center justify-center gap-2">
						Explorar Tutorías
						<ArrowRight className="ml-2 h-4 w-4" />
					</Link>
				</Button>
			</div>

			{/* Stats Cards */}
			<div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
				<Card>
					<CardHeader className="flex flex-row items-center justify-between pb-2">
						<CardTitle className="text-sm font-medium text-muted-foreground">
							Tutorías Disponibles
						</CardTitle>
						<BookOpen className="h-4 w-4 text-primary" />
					</CardHeader>
					<CardContent>
						<div className="text-2xl font-bold">{upcomingTutorias.length}</div>
						<p className="text-xs text-muted-foreground">Próximas</p>
					</CardContent>
				</Card>
				<Card>
					<CardHeader className="flex flex-row items-center justify-between pb-2">
						<CardTitle className="text-sm font-medium text-muted-foreground">
							Mi Rol
						</CardTitle>
						<Users className="h-4 w-4 text-primary" />
					</CardHeader>
					<CardContent>
						<div className="text-2xl font-bold capitalize">{user?.role || "—"}</div>
						<p className="text-xs text-muted-foreground">En la plataforma</p>
					</CardContent>
				</Card>
				<Card>
					<CardHeader className="flex flex-row items-center justify-between pb-2">
						<CardTitle className="text-sm font-medium text-muted-foreground">
							Postulaciones
						</CardTitle>
						<TrendingUp className="h-4 w-4 text-primary" />
					</CardHeader>
					<CardContent>
						<div className="text-lg font-medium">
							<Link to="/postulacion-tutor" className="text-sky-600 hover:underline">
								Ser Tutor
							</Link>
						</div>
						<p className="text-xs text-muted-foreground">Postulate aquí</p>
					</CardContent>
				</Card>
				<Card>
					<CardHeader className="flex flex-row items-center justify-between pb-2">
						<CardTitle className="text-sm font-medium text-muted-foreground">
							Calendar
						</CardTitle>
						<Calendar className="h-4 w-4 text-primary" />
					</CardHeader>
					<CardContent>
						<div className="text-lg font-medium">
							<Link to="/dashboard/mis-inscripciones" className="text-sky-600 hover:underline">
								Mis Inscripciones
							</Link>
						</div>
						<p className="text-xs text-muted-foreground">Tus tutorías</p>
					</CardContent>
				</Card>
			</div>

			{/* Main Content Grid */}
			<div className="grid gap-6 lg:grid-cols-3">
				{/* Upcoming Tutorias */}
				<Card className="lg:col-span-2">
					<CardHeader>
						<div className="flex items-center justify-between">
							<div>
								<CardTitle className="text-lg">Próximas Tutorías</CardTitle>
								<CardDescription>Tutorías disponibles para inscribirte</CardDescription>
							</div>
							<Button variant="outline" size="sm" asChild>
								<Link to="/dashboard/tutorias">Ver todas</Link>
							</Button>
						</div>
					</CardHeader>
					<CardContent>
						{upcomingTutorias.length > 0 ? (
							<div className="space-y-4">
								{upcomingTutorias.map((tutoria) => (
									<div
										key={tutoria.id}
										className="rounded-lg border p-4 transition-colors hover:bg-muted/50"
									>
										<div className="flex items-start justify-between gap-3">
											<div className="space-y-1">
												<div className="flex items-center gap-2">
													<h4 className="font-medium">{tutoria.nombre || tutoria.titulo}</h4>
													<Badge variant={tutoria.modalidad === "VIRTUAL" ? "secondary" : "outline"}>
														{tutoria.modalidad}
													</Badge>
												</div>
												<p className="text-sm text-muted-foreground">
													{tutoria.materia?.nombre || "—"}
												</p>
												{tutoria.fecha && (
													<div className="flex items-center gap-4 text-xs text-muted-foreground">
														<span className="flex items-center gap-1">
															<Calendar className="h-3 w-3" />
															{format(new Date(tutoria.fecha), "d 'de' MMMM", { locale: es })}
														</span>
														{tutoria.hora_inicio && (
															<span className="flex items-center gap-1">
																<Clock className="h-3 w-3" />
																{tutoria.hora_inicio?.slice(0, 5)} - {tutoria.hora_fin?.slice(0, 5)}
															</span>
														)}
													</div>
												)}
											</div>
											<Button size="sm" variant="outline" onClick={() => toggleTutoriaDetails(tutoria.id)}>
												{expandedTutoriaId === tutoria.id ? (
													<>
														<ChevronUp className="mr-2 h-4 w-4" />
														Ocultar
													</>
												) : (
													<>
														<ChevronDown className="mr-2 h-4 w-4" />
														Ver más
													</>
												)}
											</Button>
										</div>

										{expandedTutoriaId === tutoria.id && (
											<div className="mt-3 space-y-2 rounded-md border border-slate-200 bg-slate-50 p-3 text-sm text-slate-600">
												<p>{tutoria.descripcion || "Podés ver información adicional de esta tutoría al abrir la sección de tutorías completas."}</p>
												<div className="flex flex-wrap gap-4 text-xs text-muted-foreground">
													{tutoria.sede && <span>Sede: {tutoria.sede}</span>}
													{tutoria.tutorNombre && <span>Tutor: {tutoria.tutorNombre}</span>}
												</div>
												{tutoria.linkDrive && (
													<a
														href={tutoria.linkDrive}
														target="_blank"
														rel="noopener noreferrer"
														className="inline-flex text-sky-600 hover:underline"
													>
														Abrir contenido de Drive
													</a>
												)}
											</div>
										)}
									</div>
								))}
							</div>
						) : (
							<div className="flex flex-col items-center justify-center py-8 text-center">
								<BookOpen className="h-12 w-12 text-muted-foreground/50 mb-4" />
								<p className="text-muted-foreground">No hay tutorías disponibles</p>
								<p className="text-sm text-muted-foreground">Vuelve más tarde</p>
							</div>
						)}
					</CardContent>
				</Card>

				{/* Announcements */}
				<Card>
					<CardHeader>
						<div className="flex items-center justify-between">
							<div className="flex items-center gap-2">
								<Bell className="h-5 w-5 text-primary" />
								<CardTitle className="text-lg">Avisos</CardTitle>
							</div>
							<Button variant="outline" size="sm" asChild>
								<Link to={user?.role === "tutor" || user?.role === "admin" ? "/dashboard/gestionar-avisos" : "/dashboard/bandeja-avisos"}>
									Ver todos
								</Link>
							</Button>
						</div>
					</CardHeader>
					<CardContent>
						{avisos.length > 0 ? (
							<div className="space-y-4">
								{avisos.map((aviso) => (
									<div key={aviso.id} className="space-y-1 border-b pb-4 last:border-0 last:pb-0">
										<h4 className="font-medium text-sm">{aviso.titulo}</h4>
										<p className="text-xs text-muted-foreground line-clamp-2">
											{aviso.contenido}
										</p>
										<p className="text-xs text-muted-foreground">
											{format(new Date(aviso.fechaCreacion), "d MMM yyyy", { locale: es })}
										</p>
										<p className="text-xs text-muted-foreground">
											{aviso.nombreTutor} &middot; {aviso.nombreTutoria}
										</p>
									</div>
								))}
							</div>
						) : (
							<div className="flex flex-col items-center justify-center py-8 text-center">
								<Bell className="h-8 w-8 text-muted-foreground/50 mb-2" />
								<p className="text-sm text-muted-foreground">No hay avisos</p>
							</div>
						)}
					</CardContent>
				</Card>
				</div>
			</div>
		</div>
	);
}