import { useCallback, useEffect, useState } from "react";
import { axiosInstance } from "@/utils/axios";
import { useAuthStore } from "@/store/auth.store";
import toast from "react-hot-toast";
import { CheckCircle, Trash2, Eye } from "lucide-react";

import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Loader2 } from "lucide-react";

export default function ValidacionTutorPage() {
	const user = useAuthStore((state) => state.user);
	const [postulaciones, setPostulaciones] = useState([]);
	const [loading, setLoading] = useState(true);
	const [updating, setUpdating] = useState(false);

	const fetchPostulaciones = useCallback(async () => {
		setLoading(true);
		try {
			const res = await axiosInstance.get("/postulaciones");
			setPostulaciones(res.data);
		} catch (error) {
			console.error("Error al obtener postulaciones:", error);
			toast.error("No se pudieron cargar las postulaciones.");
		} finally {
			setLoading(false);
		}
	}, []);

	useEffect(() => {
		if (!user) {
			return;
		}

		if (user.role === "admin") {
			fetchPostulaciones();
		}
	}, [user, fetchPostulaciones]);

	const handleDecision = async (id, estado) => {
		const comentario = window.prompt("Agrega un comentario opcional para la decisión:", "");
		if (comentario === null) return;

		setUpdating(true);
		try {
			await axiosInstance.put(`/postulaciones/${id}/estado`, null, {
				params: {
					estado: estado,
					comentario,
				},
			});
			toast.success("Estado actualizado correctamente.");
			fetchPostulaciones();
		} catch (error) {
			console.error("Error al actualizar estado:", error);
			toast.error("No se pudo cambiar el estado de la postulación.");
		} finally {
			setUpdating(false);
		}
	};

	const verPdf = async (id) => {
		try {
			const response = await axiosInstance.get(`/postulaciones/${id}/pdf`, {
				responseType: "blob",
			});

			const fileUrl = window.URL.createObjectURL(new Blob([response.data], { type: "application/pdf" }));
			window.open(fileUrl, "_blank", "noopener,noreferrer");
		} catch (error) {
			console.error("Error al abrir el PDF:", error);
			toast.error("No se pudo abrir el PDF de la postulación.");
		}
	};

	if (loading) {
		return (
			<div className="min-h-screen bg-[#F7F9FB] px-4 py-10">
				<div className="mx-auto flex w-full max-w-5xl items-center justify-center rounded-2xl bg-white p-10 shadow-sm">
					<Loader2 className="mr-3 h-6 w-6 animate-spin text-slate-600" />
					<span className="text-slate-700">Cargando postulaciones...</span>
				</div>
			</div>
		);
	}

	if (user?.role !== "admin") {
		return (
			<div className="min-h-screen bg-[#F7F9FB] px-4 py-10">
				<div className="mx-auto flex w-full max-w-5xl flex-col gap-4 rounded-2xl bg-white p-10 shadow-sm">
					<h1 className="text-3xl font-bold text-slate-900">Acceso denegado</h1>
					<p className="text-slate-600">Esta página solo está disponible para administradores.</p>
				</div>
			</div>
		);
	}

	return (
		<div className="min-h-screen bg-[#F7F9FB] px-4 py-10">
			<div className="mx-auto w-full max-w-6xl space-y-6">
				<div className="rounded-3xl bg-white p-8 shadow-sm">
					<div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
						<div>
							<h1 className="text-3xl font-bold text-slate-900">Validación de Tutores</h1>
							<p className="text-slate-600">Revisa las postulaciones de los aspirantes a tutor y acepta o rechaza cada caso.</p>
						</div>
						<div className="rounded-full bg-sky-100 px-4 py-2 text-sky-700">ADMIN</div>
					</div>
				</div>

				{postulaciones.length === 0 ? (
					<div className="rounded-3xl bg-white p-10 text-center text-slate-600 shadow-sm">
						No hay postulaciones pendientes por revisar.
					</div>
				) : (
					<div className="grid gap-6">
						{postulaciones.map((postulacion) => (
							<Card key={postulacion.id} className="border-slate-200 shadow-md">
								<CardHeader className="pb-2">
									<div className="flex flex-col gap-2 sm:flex-row sm:items-center sm:justify-between">
										<div>
											<CardTitle className="text-xl text-slate-900">
												{postulacion.postulante?.apellido}, {postulacion.postulante?.nombre}
											</CardTitle>
											<CardDescription>{postulacion.postulante?.email}</CardDescription>
										</div>
										<div className="flex flex-wrap items-center gap-2">
											<Badge
												variant={
													postulacion.status === "pendiente"
														? "secondary"
														: postulacion.status === "aprobada"
															? "success"
															: "destructive"
												}
											>
												{postulacion.status === "pendiente"
													? "Pendiente"
													: postulacion.status === "aprobada"
														? "Aprobada"
														: "Rechazada"}
											</Badge>
											<Button type="button" variant="outline" size="sm" onClick={() => verPdf(postulacion.id)}>
												<Eye className="mr-2 h-4 w-4" />
												Ver PDF
											</Button>
										</div>
									</div>
								</CardHeader>

								<CardContent className="space-y-4">
									<div className="grid gap-3 sm:grid-cols-3">
										<div>
											<p className="text-sm text-slate-500">Materia</p>
											<p className="text-base font-medium text-slate-800">{postulacion.materia?.nombre}</p>
										</div>
										<div>
											<p className="text-sm text-slate-500">Nota</p>
											<p className="text-base font-medium text-slate-800">{postulacion.nota_aprobacion}</p>
										</div>
										<div>
											<p className="text-sm text-slate-500">Modalidad</p>
											<p className="text-base font-medium text-slate-800">{postulacion.modalidad_preferencia}</p>
										</div>
									</div>

									<div className="grid gap-3 sm:grid-cols-2">
										<div>
											<p className="text-sm text-slate-500">Sede preferida</p>
											<p className="text-base font-medium text-slate-800">{postulacion.sede_preferencia}</p>
										</div>
										<div>
											<p className="text-sm text-slate-500">Comentario del admin</p>
											<p className="text-base font-medium text-slate-800">{postulacion.admin_comentario || "Sin comentario"}</p>
										</div>
									</div>

									<div>
										<p className="text-sm text-slate-500">Justificación</p>
										<p className="whitespace-pre-line rounded-2xl bg-slate-50 p-4 text-slate-700">{postulacion.justificacion}</p>
									</div>

									<div className="flex flex-col gap-3 sm:flex-row sm:justify-end">
										<Button
											type="button"
											disabled={postulacion.status !== "pendiente" || updating}
											onClick={() => handleDecision(postulacion.id, "APROBADA")}
											className="bg-emerald-600 text-white hover:bg-emerald-700"
										>
											<CheckCircle className="mr-2 h-4 w-4" />
											Aceptar
										</Button>
										<Button
											type="button"
											variant="destructive"
											disabled={postulacion.status !== "pendiente" || updating}
											onClick={() => handleDecision(postulacion.id, "RECHAZADA")}
										>
											<Trash2 className="mr-2 h-4 w-4" />
											Rechazar
										</Button>
									</div>
								</CardContent>
							</Card>
						))}
					</div>
				)}
			</div>
		</div>
	);
}
