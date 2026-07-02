import { useEffect, useMemo, useState } from "react";
import { BadgeCheck, Loader2, MessageSquareQuote, Save, Star, TrendingUp } from "lucide-react";
import { format } from "date-fns";
import { es } from "date-fns/locale";

import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Input } from "@/components/ui/input";
import { axiosInstance } from "@/utils/axios";
import { useAuthStore } from "@/store/auth.store";

function PerfilTutorPage() {
	const { user } = useAuthStore();
	const updateLocalUser = useAuthStore((state) => state.updateLocalUser);
	const [perfil, setPerfil] = useState(null);
	const [mensajeError, setMensajeError] = useState("");
	const [mensajeEstado, setMensajeEstado] = useState("");
	const [cargando, setCargando] = useState(true);
	const [guardando, setGuardando] = useState(false);
	const [datosPersonales, setDatosPersonales] = useState({
		nombre: "",
		apellido: "",
		email: "",
	});

	useEffect(() => {
		const cargarPerfil = async () => {
			if (!user?.id) return;

			try {
				setCargando(true);
				setMensajeError("");
				setMensajeEstado("");
				const [perfilResponse, usuarioResponse] = await Promise.all([
					axiosInstance.get(`/perfil-tutor/${user.id}`),
					axiosInstance.get(`/auth/usuarios/${user.id}`),
				]);

				setPerfil(perfilResponse.data);
				setDatosPersonales({
					nombre: usuarioResponse.data?.nombre || user.nombre || "",
					apellido: usuarioResponse.data?.apellido || user.apellido || "",
					email: usuarioResponse.data?.email || user.email || "",
				});
			} catch (error) {
				console.error("Error al cargar el perfil del tutor:", error);
				const status = error.response?.status;
				if (status === 404) {
					setMensajeError("No hay un perfil de tutor aprobado para esta cuenta.");
				} else if (status === 403) {
					setMensajeError("No tenés permisos para ver este perfil.");
				} else {
					setMensajeError("No se pudo cargar el perfil del tutor.");
				}
				setPerfil(null);
			} finally {
				setCargando(false);
			}
		};

		cargarPerfil();
	}, [user?.id, user?.nombre, user?.apellido, user?.email]);

	const nombreCompleto = useMemo(() => {
		const nombre = datosPersonales.nombre.trim();
		const apellido = datosPersonales.apellido.trim();
		return `${nombre} ${apellido}`.trim() || perfil?.nombreCompleto || `${user?.nombre || "Usuario"} ${user?.apellido || ""}`.trim();
	}, [datosPersonales.apellido, datosPersonales.nombre, perfil?.nombreCompleto, user?.apellido, user?.nombre]);

	const iniciales = useMemo(() => {
		const nombre = datosPersonales.nombre || user?.nombre || "U";
		const apellido = datosPersonales.apellido || user?.apellido || "";
		return `${nombre.charAt(0)}${apellido.charAt(0)}`.toUpperCase();
	}, [datosPersonales.apellido, datosPersonales.nombre, user?.apellido, user?.nombre]);

	const handleChange = (campo) => (event) => {
		setDatosPersonales((actual) => ({
			...actual,
			[campo]: event.target.value,
		}));
	};

	const handleGuardar = async (event) => {
		event.preventDefault();

		if (!datosPersonales.nombre.trim() || !datosPersonales.apellido.trim()) {
			setMensajeError("Completá nombre y apellido antes de guardar.");
			return;
		}

		setGuardando(true);
		try {
			const response = await axiosInstance.put(`/auth/usuarios/${user.id}`, {
				nombre: datosPersonales.nombre.trim(),
				apellido: datosPersonales.apellido.trim(),
			});
			updateLocalUser({
				nombre: response.data?.nombre || datosPersonales.nombre.trim(),
				apellido: response.data?.apellido || datosPersonales.apellido.trim(),
				email: response.data?.email || datosPersonales.email.trim().toLowerCase(),
			});
			setPerfil((actual) => ({
				...actual,
				nombreCompleto: `${response.data?.nombre || datosPersonales.nombre.trim()} ${response.data?.apellido || datosPersonales.apellido.trim()}`.trim(),
			}));
			setMensajeError("");
			setMensajeEstado("Cambios guardados correctamente.");
		} finally {
			setGuardando(false);
		}
	};

	if (cargando) {
		return (
			<div className="flex min-h-[60vh] items-center justify-center">
				<Loader2 className="h-8 w-8 animate-spin text-sky-500" />
			</div>
		);
	}

	const cantidadResenas = perfil?.cantidadResenas || 0;
	const promedioCalificacion = Number(perfil?.promedioCalificacion || 0);
	const tutoriasActivas = perfil?.tutoriasActivas || [];
	const resenasRecientes = perfil?.resenasRecientes || [];

	return (
		<div className="min-h-screen bg-slate-50">
			<div className="container mx-auto space-y-6 px-6 py-8">
				<div className="grid gap-6 lg:grid-cols-[1.45fr_0.85fr]">
					<Card className="border-slate-200 shadow-sm">
						<CardHeader className="space-y-2 border-b border-slate-100 pb-6">
							<CardTitle className="text-2xl text-slate-900">Mi Perfil</CardTitle>
							<CardDescription>Gestioná tu información personal y revisá tu resumen público como tutor.</CardDescription>
						</CardHeader>
						<CardContent className="space-y-6 pt-6">
							{mensajeError ? (
								<div className="rounded-xl border border-amber-200 bg-amber-50 p-4 text-sm text-amber-900">
									{mensajeError}
								</div>
							) : null}
							{mensajeEstado ? (
								<div className="rounded-xl border border-emerald-200 bg-emerald-50 p-4 text-sm text-emerald-900">
									{mensajeEstado}
								</div>
							) : null}

							<form onSubmit={handleGuardar} className="space-y-6">
								<div className="flex items-start gap-4 rounded-2xl bg-slate-50 p-5">
									<div className="flex h-16 w-16 items-center justify-center rounded-full bg-sky-600 text-xl font-bold text-white">
										{iniciales}
									</div>
									<div className="space-y-2">
										<div className="flex flex-wrap items-center gap-2">
											<h3 className="text-lg font-semibold text-slate-900">{nombreCompleto}</h3>
											<Badge variant="secondary">{user?.role === "admin" ? "Administrador" : "Tutor"}</Badge>
										</div>
										<p className="text-sm text-slate-500">{datosPersonales.email}</p>
										<p className="text-sm text-slate-500">
											{perfil?.titulo || "Sin título informado"} · {perfil?.carrera || "Sin carrera informada"}
										</p>
									</div>
								</div>

								<div className="grid gap-4 sm:grid-cols-2">
									<div className="space-y-2">
										<label className="text-sm font-medium text-slate-700">Nombre</label>
										<Input value={datosPersonales.nombre} onChange={handleChange("nombre")} placeholder="Tu nombre" />
									</div>
									<div className="space-y-2">
										<label className="text-sm font-medium text-slate-700">Apellido</label>
										<Input value={datosPersonales.apellido} onChange={handleChange("apellido")} placeholder="Tu apellido" />
									</div>
								</div>

								<div className="space-y-2">
									<label className="text-sm font-medium text-slate-700">Correo institucional</label>
									<Input value={datosPersonales.email} readOnly type="email" className="bg-slate-100" />
								</div>

								<div className="flex flex-wrap gap-2">
									<Button type="submit" className="bg-sky-600 text-white hover:bg-sky-700" disabled={guardando}>
										<Save className="mr-2 h-4 w-4" />
										{guardando ? "Guardando..." : "Guardar cambios"}
									</Button>
								</div>
							</form>
						</CardContent>
					</Card>

					<div className="space-y-6">
						<Card className="border-slate-200 shadow-sm">
							<CardHeader>
								<CardTitle className="flex items-center gap-2 text-lg text-slate-900">
									<TrendingUp className="h-5 w-5 text-sky-600" />
									Resumen del Tutor
								</CardTitle>
							</CardHeader>
							<CardContent className="space-y-4 pt-0">
								<div className="grid grid-cols-2 gap-3">
									<div className="rounded-xl bg-slate-50 p-4">
										<p className="text-xs uppercase tracking-wide text-slate-500">Promedio</p>
										<p className="mt-2 text-2xl font-bold text-slate-900">{promedioCalificacion.toFixed(1)}</p>
									</div>
									<div className="rounded-xl bg-slate-50 p-4">
										<p className="text-xs uppercase tracking-wide text-slate-500">Reseñas</p>
										<p className="mt-2 text-2xl font-bold text-slate-900">{cantidadResenas}</p>
									</div>
								</div>
								<div className="grid grid-cols-2 gap-3">
									<div className="rounded-xl bg-slate-50 p-4">
										<p className="text-xs uppercase tracking-wide text-slate-500">Tutorías activas</p>
										<p className="mt-2 text-2xl font-bold text-slate-900">{tutoriasActivas.length}</p>
									</div>
									<div className="rounded-xl bg-slate-50 p-4">
										<p className="text-xs uppercase tracking-wide text-slate-500">Materias</p>
										<p className="mt-2 text-2xl font-bold text-slate-900">{perfil?.materias?.length || 0}</p>
									</div>
								</div>
							</CardContent>
						</Card>

						<Card className="border-slate-200 shadow-sm">
							<CardHeader>
								<CardTitle className="flex items-center gap-2 text-lg text-slate-900">
									<BadgeCheck className="h-5 w-5 text-emerald-600" />
									Información de cuenta
								</CardTitle>
							</CardHeader>
							<CardContent className="pt-0 text-sm text-slate-600">
								<p>Tu correo institucional se mantiene como identificador de acceso.</p>
							</CardContent>
						</Card>
					</div>
				</div>

				<Card className="border-slate-200 shadow-sm">
					<CardHeader>
						<CardTitle className="flex items-center gap-2 text-lg text-slate-900">
							<MessageSquareQuote className="h-5 w-5 text-sky-600" />
							Reseñas recientes
						</CardTitle>
						<CardDescription>Comentarios y calificaciones recibidos de tus alumnos</CardDescription>
					</CardHeader>
					<CardContent className="pt-0">
						{resenasRecientes.length === 0 ? (
							<div className="rounded-xl border border-dashed border-slate-300 bg-white p-8 text-center text-sm text-slate-600">
								Todavía no recibiste reseñas.
							</div>
						) : (
							<div className="grid gap-4 md:grid-cols-2 xl:grid-cols-3">
								{resenasRecientes.map((feedback) => (
									<div key={feedback.id} className="rounded-xl border border-slate-200 bg-white p-4 shadow-sm">
										<div className="flex items-start justify-between gap-3">
											<div>
												<p className="font-semibold text-slate-900">{feedback.nombreTutoria || "Tutoría sin nombre"}</p>
												<p className="text-xs text-slate-500">{feedback.nombreAlumno || "Estudiante anónimo"}</p>
											</div>
											<div className="flex items-center gap-1 text-amber-500">
												<Star className="h-4 w-4 fill-current" />
												<span className="text-sm font-semibold text-slate-900">{feedback.calificacion}/5</span>
											</div>
										</div>
										<p className="mt-3 text-sm text-slate-700">{feedback.comentarios || "Sin comentario"}</p>
										<p className="mt-4 text-xs text-slate-500">
											{feedback.fecha ? format(new Date(feedback.fecha), "d 'de' MMMM yyyy", { locale: es }) : "Sin fecha"}
										</p>
									</div>
								))}
							</div>
						)}
					</CardContent>
				</Card>
			</div>
		</div>
	);
}

export default PerfilTutorPage;