import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { ArrowLeft, Award, BookOpen, GraduationCap, Mail, Star, Users, Calendar, Clock, MessageSquare } from "lucide-react";

import { useAuthStore } from "@/store/auth.store";
import { axiosInstance } from "@/utils/axios";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";

export default function PerfilTutorPage() {
	const { user } = useAuthStore();
	const { tutorId } = useParams();
	const [perfil, setPerfil] = useState(null);
	const [loading, setLoading] = useState(true);

	useEffect(() => {
		const cargarPerfil = async () => {
			try {
				const idObjetivo = tutorId || user?.id;

				if (idObjetivo) {
					const response = await axiosInstance.get(`/perfil-tutor/${idObjetivo}`);
					setPerfil(response.data);
				} else {
					setPerfil({
						nombreCompleto: `${user?.nombre || "Usuario"} ${user?.apellido || ""}`.trim(),
						email: user?.email,
						titulo: "Perfil de alumno",
						carrera: "",
						promedioCalificacion: null,
						cantidadResenas: 0,
						cantidadTutoriasActivas: 0,
						materias: [],
						tutoriasActivas: [],
						resenasRecientes: [],
					});
				}
			} catch (error) {
				console.error("Error al cargar el perfil:", error);
			} finally {
				setLoading(false);
			}
		};

		cargarPerfil();
	}, [user, tutorId]);

	if (loading) {
		return (
			<div className="flex items-center justify-center py-20">
				<div className="h-8 w-8 animate-spin rounded-full border-4 border-sky-500 border-t-transparent" />
			</div>
		);
	}

	const nombreCompleto = perfil?.nombreCompleto || `${user?.nombre || "Usuario"} ${user?.apellido || ""}`.trim();
	const esMiPerfil = !tutorId;
	const volverHref = tutorId ? "/dashboard/tutorias" : "/dashboard";

	return (
		<div className="min-h-screen bg-[#F7F9FB] px-4 py-10 sm:px-6 lg:px-8">
			<div className="mx-auto flex w-full max-w-5xl flex-col gap-6">
				<Button asChild variant="outline" className="w-fit border-sky-200 text-sky-700 hover:bg-sky-50 hover:text-sky-800">
					<Link to={volverHref} className="inline-flex items-center gap-2">
						<ArrowLeft className="h-4 w-4" />
						{tutorId ? "Volver a tutorías" : "Volver al inicio"}
					</Link>
				</Button>

				<Card className="border-slate-200 shadow-md">
					<CardHeader>
						<CardTitle className="text-2xl text-slate-900">{esMiPerfil ? "Mi Perfil" : "Perfil de Tutor"}</CardTitle>
						<CardDescription>
							{esMiPerfil
								? "Información general de tu cuenta y, si sos tutor, tu perfil académico."
								: "Perfil público del tutor, con calificación, reseñas y tutorías activas."}
						</CardDescription>
					</CardHeader>
					<CardContent className="space-y-6">
						<div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
							<Card className="border-slate-200 shadow-sm">
								<CardContent className="p-4">
									<p className="text-xs uppercase tracking-wide text-slate-500">Nombre</p>
									<p className="mt-1 font-semibold text-slate-900">{nombreCompleto}</p>
								</CardContent>
							</Card>
							<Card className="border-slate-200 shadow-sm">
								<CardContent className="p-4">
									<p className="text-xs uppercase tracking-wide text-slate-500">Email</p>
									<p className="mt-1 font-semibold text-slate-900 break-all">{perfil?.email || user?.email}</p>
								</CardContent>
							</Card>
							<Card className="border-slate-200 shadow-sm">
								<CardContent className="p-4">
									<p className="text-xs uppercase tracking-wide text-slate-500">Rol</p>
									<p className="mt-1 font-semibold text-slate-900 capitalize">{user?.role || "—"}</p>
								</CardContent>
							</Card>
							<Card className="border-slate-200 shadow-sm">
								<CardContent className="p-4">
									<p className="text-xs uppercase tracking-wide text-slate-500">Tutorías activas</p>
									<p className="mt-1 font-semibold text-slate-900">{perfil?.cantidadTutoriasActivas ?? 0}</p>
								</CardContent>
							</Card>
						</div>

						<div className="grid gap-6">
							<Card className="border-slate-200 shadow-sm">
								<CardHeader>
									<div className="flex items-center gap-2">
										<GraduationCap className="h-5 w-5 text-sky-600" />
										<CardTitle className="text-lg text-slate-900">Perfil tutor</CardTitle>
									</div>
									<CardDescription>{perfil?.titulo || "Sin título"}</CardDescription>
								</CardHeader>
								<CardContent className="space-y-3 text-sm text-slate-700">
									<div className="flex items-center gap-2">
										<Mail className="h-4 w-4 text-slate-500" />
										<span>{perfil?.email || user?.email}</span>
									</div>
									<div className="flex items-center gap-2">
										<BookOpen className="h-4 w-4 text-slate-500" />
										<span>{perfil?.carrera || "Carrera no disponible"}</span>
									</div>
									<div className="flex items-center gap-2">
										<Users className="h-4 w-4 text-slate-500" />
										<span>{perfil?.cantidadResenas ?? 0} reseñas</span>
									</div>
									<div className="flex items-center gap-2">
										<Star className="h-4 w-4 text-amber-500" />
										<span>{perfil?.promedioCalificacion ?? "—"} promedio</span>
									</div>
									<div className="flex items-center gap-2">
										<Award className="h-4 w-4 text-sky-600" />
										<span>{perfil?.materias?.length || 0} materias asignadas</span>
									</div>
								</CardContent>
							</Card>

							<Card className="border-slate-200 shadow-sm">
								<CardHeader>
									<CardTitle className="text-lg text-slate-900">Materias</CardTitle>
									<CardDescription>Listado de materias asociadas al tutor.</CardDescription>
								</CardHeader>
								<CardContent className="space-y-2">
									{perfil?.materias?.length > 0 ? (
										perfil.materias.map((materia) => (
											<div key={materia.id} className="rounded-xl border border-slate-200 bg-slate-50 px-4 py-3">
												<p className="font-medium text-slate-900">{materia.nombre}</p>
											</div>
										))
									) : (
										<p className="text-sm text-slate-500">No hay materias para mostrar.</p>
									)}
								</CardContent>
							</Card>
						</div>

						<div className="grid gap-6 lg:grid-cols-[1fr_1fr]">
							<Card className="border-slate-200 shadow-sm">
								<CardHeader>
									<div className="flex items-center gap-2">
										<MessageSquare className="h-5 w-5 text-sky-600" />
										<CardTitle className="text-lg text-slate-900">Reseñas recientes</CardTitle>
									</div>
									<CardDescription>Comentarios y calificaciones de estudiantes.</CardDescription>
								</CardHeader>
								<CardContent className="space-y-3">
									{perfil?.resenasRecientes?.length > 0 ? (
										perfil.resenasRecientes.map((resena) => (
											<div key={resena.id} className="rounded-xl border border-slate-200 bg-slate-50 px-4 py-3 text-sm text-slate-700">
												<div className="flex items-center justify-between gap-3">
													<p className="font-medium text-slate-900">{resena.nombreAlumno}</p>
													<span className="inline-flex items-center rounded-full bg-amber-100 px-2 py-0.5 text-xs font-semibold text-amber-700">
														<Star className="mr-1 h-3.5 w-3.5" />
														{resena.calificacion}
													</span>
												</div>
												<p className="mt-1 font-medium text-slate-900">{resena.nombreTutoria}</p>
												<p className="mt-1 text-slate-600">{resena.comentarios || "Sin comentarios."}</p>
												<p className="mt-2 text-xs text-slate-500">{resena.fecha ? new Date(resena.fecha).toLocaleString("es-AR") : "Sin fecha"}</p>
											</div>
										))
									) : (
										<p className="text-sm text-slate-500">Todavía no hay reseñas para mostrar.</p>
									)}
								</CardContent>
							</Card>

							<Card className="border-slate-200 shadow-sm">
								<CardHeader>
									<CardTitle className="text-lg text-slate-900">Materias</CardTitle>
									<CardDescription>Listado de materias asociadas al tutor.</CardDescription>
								</CardHeader>
								<CardContent className="space-y-3">
									{perfil?.materias?.length > 0 ? (
										perfil.materias.map((materia) => (
											<div key={materia.id} className="rounded-xl border border-slate-200 bg-slate-50 px-4 py-3">
												<p className="font-medium text-slate-900">{materia.nombre}</p>
											</div>
										))
									) : (
										<p className="text-sm text-slate-500">No hay materias para mostrar.</p>
									)}
								</CardContent>
							</Card>

							<Card className="border-slate-200 shadow-sm">
								<CardHeader>
									<div className="flex items-center gap-2">
										<Calendar className="h-5 w-5 text-sky-600" />
										<CardTitle className="text-lg text-slate-900">Tutorías activas</CardTitle>
									</div>
									<CardDescription>Tutorías vigentes y próximas del tutor.</CardDescription>
								</CardHeader>
								<CardContent className="space-y-3">
									{perfil?.tutoriasActivas?.length > 0 ? (
										perfil.tutoriasActivas.map((tutoria) => (
											<div key={tutoria.id} className="rounded-xl border border-slate-200 bg-slate-50 px-4 py-3 text-sm text-slate-700">
												<p className="font-medium text-slate-900">{tutoria.nombre}</p>
												<p>{tutoria.materiaNombre || "Sin materia"} · {tutoria.sede || "Sin sede"}</p>
												<p>{tutoria.modalidad} · {tutoria.fecha}</p>
											</div>
										))
									) : (
										<p className="text-sm text-slate-500">No hay tutorías activas para mostrar.</p>
									)}
								</CardContent>
							</Card>
						</div>
					</CardContent>
				</Card>
			</div>
		</div>
	);
}