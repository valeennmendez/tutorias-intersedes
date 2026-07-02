import { useCallback, useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { Bell, BookOpen, Calendar, Clock, MessageSquare, Plus, Star, Trash2, PencilLine, Users } from "lucide-react";
import { format } from "date-fns";
import { es } from "date-fns/locale";

import { useAuthStore } from "@/store/auth.store";
import { axiosInstance } from "@/utils/axios";
import { tutoriaStore } from "@/store/tutorias.store";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import {
	Dialog,
	DialogContent,
	DialogDescription,
	DialogFooter,
	DialogHeader,
	DialogTitle,
} from "@/components/ui/dialog";

export default function PanelTutorPage() {
	const { user } = useAuthStore();
	const [perfilTutor, setPerfilTutor] = useState(null);
	const [tutorias, setTutorias] = useState([]);
	const [avisos, setAvisos] = useState([]);
	const [loading, setLoading] = useState(true);
	const [tutoriaAEliminar, setTutoriaAEliminar] = useState(null);

	const esFutura = (tutoria) => {
		if (!tutoria?.fecha) return false;

		const inicio = tutoria.horaInicio ? `${tutoria.fecha}T${tutoria.horaInicio}` : `${tutoria.fecha}T23:59:59`;
		return new Date(inicio) >= new Date();
	};

	const cargarDatos = useCallback(async () => {
		try {
			const [tutoriasRes, perfilRes, avisosRes] = await Promise.allSettled([
				axiosInstance.get("/tutorias"),
				user?.id ? axiosInstance.get(`/perfil-tutor/${user.id}`) : Promise.resolve(null),
				user?.role === "tutor" || user?.role === "admin"
					? axiosInstance.get("/avisos/mis-avisos-enviados")
					: Promise.resolve(null),
			]);

			const tutoriasData = tutoriasRes.status === "fulfilled" ? tutoriasRes.value.data || [] : [];
			const ordenadas = [...tutoriasData].sort((a, b) => {
				const fechaA = new Date(`${a.fecha || "9999-12-31"}T${a.horaInicio || "23:59:59"}`);
				const fechaB = new Date(`${b.fecha || "9999-12-31"}T${b.horaInicio || "23:59:59"}`);
				return fechaA - fechaB;
			});

			const propias = ordenadas.filter((tutoria) => Number(tutoria.tutorId) === Number(user?.id));
			setTutorias(propias);
			if (perfilRes.status === "fulfilled" && perfilRes.value) setPerfilTutor(perfilRes.value.data);

			if (avisosRes.status === "fulfilled" && avisosRes.value) {
				setAvisos(avisosRes.value.data?.slice(0, 3) || []);
			}
		} catch (error) {
			console.error("Error al cargar el panel tutor:", error);
		} finally {
			setLoading(false);
		}
	}, [user]);

	useEffect(() => {
		let cancelado = false;
		Promise.resolve().then(() => {
			if (!cancelado) {
				void cargarDatos();
			}
		});

		return () => {
			cancelado = true;
		};
	}, [cargarDatos]);

	const nombreCompleto = `${user?.nombre || "Tutor"} ${user?.apellido || ""}`.trim();

	if (loading) {
		return (
			<div className="flex items-center justify-center py-20">
				<div className="h-8 w-8 animate-spin rounded-full border-4 border-sky-500 border-t-transparent" />
			</div>
		);
	}

	const proximasTutorias = tutorias.filter(esFutura).slice(0, 3);

	const handleEliminar = async (id) => {
		await tutoriaStore.getState().eliminarTutoria(id);
		setTutoriaAEliminar(null);
		await cargarDatos();
	};

	const stats = [
		{ label: "Total Tutorías", value: tutorias.length, icon: BookOpen },
		{ label: "Tutorías Activas", value: perfilTutor?.cantidadTutoriasActivas ?? proximasTutorias.length, icon: Calendar },
		{ label: "Alumnos Ayudados", value: perfilTutor?.cantidadAlumnosAyudados ?? 0, icon: Users },
		{ label: "Calificación", value: perfilTutor?.promedioEstrellas ?? "-", icon: Star },
	];

	return (
		<div className="min-h-screen bg-[#F7F9FB]">
			<div className="px-4 py-10">
				<div className="mx-auto flex w-full max-w-5xl flex-col gap-6">
					<div className="flex items-center justify-between gap-4">
						<div>
							<h1 className="text-2xl font-bold text-slate-900">Panel de Tutor</h1>
							<p className="text-sm text-slate-500">Bienvenido, {nombreCompleto}. Gestioná tus tutorías desde acá.</p>
						</div>
						<Button asChild className="bg-[#008BBA] text-white hover:bg-[#00779f]">
							<Link to="/crear-tutoria" className="flex items-center gap-2">
								<Plus className="h-4 w-4" />
								Nueva Tutoría
							</Link>
						</Button>
					</div>

					<div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
						{stats.map((stat) => {
							const Icon = stat.icon;
							return (
								<Card key={stat.label} className="border-slate-200 shadow-md">
									<CardContent className="p-5">
										<div className="flex items-start justify-between gap-4">
											<div className="space-y-2">
												<p className="text-xs font-medium uppercase tracking-wide text-slate-500">{stat.label}</p>
												<p className="text-3xl font-bold text-slate-900">{stat.value}</p>
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

						<div className="grid gap-6 lg:grid-cols-[1.4fr_0.8fr]">
							<Card className="border-slate-200 shadow-md">
							<CardHeader className="flex flex-row items-start justify-between gap-4">
								<div>
									<CardTitle className="text-xl text-slate-900">Próximas Tutorías</CardTitle>
									<CardDescription>Tutorías programadas para los próximos días</CardDescription>
								</div>
								<Button variant="outline" size="sm" asChild>
									<Link to="/dashboard/tutorias">Ver todas</Link>
								</Button>
							</CardHeader>
							<CardContent className="space-y-3 pt-0">
								{proximasTutorias.length > 0 ? (
									proximasTutorias.map((tutoria) => (
										<div key={tutoria.id} className="flex flex-col gap-4 rounded-2xl border border-slate-200 bg-white p-4 shadow-sm sm:flex-row sm:items-center sm:justify-between">
											<div className="space-y-1">
												<div className="flex flex-wrap items-center gap-2">
													<h4 className="text-base font-semibold text-slate-900">{tutoria.nombre || tutoria.titulo}</h4>
													<Badge variant={tutoria.modalidad === "VIRTUAL" ? "secondary" : "outline"}>{tutoria.modalidad}</Badge>
												</div>
												<p className="text-sm text-slate-600">{tutoria.materiaNombre || "—"}</p>
												<div className="flex flex-wrap items-center gap-4 text-xs text-slate-500">
													<span className="flex items-center gap-1"><Calendar className="h-3.5 w-3.5" />{tutoria.fecha ? format(new Date(tutoria.fecha), "d 'de' MMMM", { locale: es }) : "Sin fecha"}</span>
													<span className="flex items-center gap-1"><Clock className="h-3.5 w-3.5" />{tutoria.horaInicio?.slice(0, 5)} - {tutoria.horaFin?.slice(0, 5)}</span>
													<span className="flex items-center gap-1"><Users className="h-3.5 w-3.5" />{tutoria.cantidadInscriptos ?? 0} / {tutoria.cupo ?? "-"}</span>
												</div>
											</div>
											<div className="flex items-center gap-3">
												<Button
													size="sm"
													variant="outline"
													className="h-10 rounded-xl border-sky-200 px-4 text-sky-700 hover:bg-sky-50 hover:text-sky-800"
													asChild
												>
													<Link to={`/dashboard/tutorias/${tutoria.id}/editar`}>
														<PencilLine className="mr-2 h-4 w-4 shrink-0" />
														Editar
													</Link>
												</Button>
												<Button
													size="sm"
													variant="destructive"
													className="h-10 rounded-xl px-4"
													onClick={() => setTutoriaAEliminar(tutoria)}
												>
													<Trash2 className="mr-2 h-4 w-4 shrink-0" />
													Eliminar
												</Button>
											</div>
										</div>
									))
								) : (
									<div className="flex flex-col items-center justify-center rounded-2xl border border-dashed border-slate-300 bg-slate-50 py-12 text-center">
										<BookOpen className="mb-3 h-10 w-10 text-slate-400" />
										<p className="text-sm font-medium text-slate-700">No hay tutorías creadas todavía</p>
										<p className="text-xs text-slate-500">Creá la primera desde el botón Nueva Tutoría</p>
									</div>
								)}
							</CardContent>
						</Card>

						<Card className="border-slate-200 shadow-md">
							<CardHeader className="flex flex-row items-center justify-between gap-4">
								<div className="flex items-center gap-2">
									<Bell className="h-5 w-5 text-sky-600" />
									<CardTitle className="text-xl text-slate-900">Avisos</CardTitle>
								</div>
								<Button variant="outline" size="sm" asChild>
									<Link to="/dashboard/gestionar-avisos">Ver todos</Link>
								</Button>
							</CardHeader>
							<CardContent className="pt-0">
								{avisos.length > 0 ? (
									<div className="space-y-4">
										{avisos.map((aviso) => (
											<div key={aviso.id} className="space-y-1 border-b border-slate-200 pb-4 last:border-0 last:pb-0">
												<h4 className="font-medium text-sm text-slate-900">{aviso.titulo}</h4>
												<p className="text-xs text-slate-500 line-clamp-2">{aviso.contenido}</p>
												<p className="text-xs text-slate-500">{format(new Date(aviso.fechaCreacion), "d MMM yyyy", { locale: es })}</p>
											</div>
										))}
									</div>
								) : (
									<div className="flex flex-col items-center justify-center rounded-2xl border border-dashed border-slate-300 bg-slate-50 py-12 text-center">
										<MessageSquare className="mb-2 h-8 w-8 text-slate-400" />
										<p className="text-sm text-slate-600">No hay avisos recientes</p>
									</div>
								)}
							</CardContent>
						</Card>
					</div>
				</div>
			</div>

			<Dialog open={Boolean(tutoriaAEliminar)} onOpenChange={(open) => !open && setTutoriaAEliminar(null)}>
				<DialogContent className="sm:max-w-md">
					<DialogHeader>
						<DialogTitle>Eliminar tutoría</DialogTitle>
						<DialogDescription>
							Esta acción es permanente. Se eliminará la tutoría {tutoriaAEliminar?.nombre || "seleccionada"}.
						</DialogDescription>
					</DialogHeader>
					<DialogFooter className="gap-2 sm:gap-0">
						<Button variant="outline" onClick={() => setTutoriaAEliminar(null)}>
							Cancelar
						</Button>
						<Button
							variant="destructive"
							onClick={() => {
								if (tutoriaAEliminar?.id) {
									void handleEliminar(tutoriaAEliminar.id);
								}
							}}
						>
							Eliminar
						</Button>
					</DialogFooter>
				</DialogContent>
			</Dialog>
		</div>
	);
}