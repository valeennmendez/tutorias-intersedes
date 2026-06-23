import { useState, useEffect, useRef } from "react";
import { axiosInstance } from "@/utils/axios";
import { useAuthStore } from "@/store/auth.store";
import toast from "react-hot-toast";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import { Badge } from "@/components/ui/badge";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Alert, AlertDescription } from "@/components/ui/alert";
import { GraduationCap, BookOpen, Clock, CheckCircle, XCircle, Loader2, AlertCircle, Award, Users, Send, Upload, FileText, Trash2 } from "lucide-react";
import { format } from "date-fns";
import { es } from "date-fns/locale";

export function PostulacionTutorClient() {
	const { user } = useAuthStore();
	const [materias, setMaterias] = useState([]);
	const [applications, setApplications] = useState([]);
	const [loadingMaterias, setLoadingMaterias] = useState(true);
	const [loadingApps, setLoadingApps] = useState(true);
	const [loading, setLoading] = useState(false);
	const [archivoPdf, setArchivoPdf] = useState(null);
	const [pdfError, setPdfError] = useState("");
	const fileInputRef = useRef(null);
	const [formData, setFormData] = useState({
		materia_id: "",
		justificacion: "",
		nota_aprobacion: "",
		sede_preferencia: "",
		modalidad: "",
	});

	useEffect(() => {
		fetchMaterias();
		fetchApplications();
	}, []);

	const fetchMaterias = async () => {
		try {
			const res = await axiosInstance.get("/materias");
			setMaterias(res.data);
		} catch (error) {
			console.error("Error al obtener materias:", error);
		} finally {
			setLoadingMaterias(false);
		}
	};

	const fetchApplications = async () => {
		try {
			const res = await axiosInstance.get("/postulaciones/mis-postulaciones");
			setApplications(res.data);
		} catch (error) {
			console.error("Error al obtener postulaciones:", error);
		} finally {
			setLoadingApps(false);
		}
	};

	const appliedMateriaIds = new Set(applications.map((a) => String(a.materia_id)));
	const availableMaterias = materias.filter((m) => !appliedMateriaIds.has(String(m.id)));

	const handleSubmit = async (e) => {
		e.preventDefault();

		if (!formData.materia_id) {
			toast.error("Debes seleccionar una materia");
			return;
		}

		if (!formData.justificacion.trim()) {
			toast.error("Debes escribir una justificación");
			return;
		}

		if (formData.justificacion.length < 50) {
			toast.error("La justificación debe tener al menos 50 caracteres");
			return;
		}

		const nota = parseFloat(formData.nota_aprobacion);
		if (isNaN(nota) || nota < 4 || nota > 10) {
			toast.error("La nota debe estar entre 4 y 10");
			return;
		}

		if (!formData.sede_preferencia) {
			toast.error("Debes seleccionar una sede preferida");
			return;
		}

		if (!formData.modalidad) {
			toast.error("Debes seleccionar una modalidad");
			return;
		}

		if (!archivoPdf) {
			toast.error("Debes adjuntar tu analítico en formato PDF");
			return;
		}

		if (archivoPdf.type !== "application/pdf") {
			toast.error("El archivo debe ser un PDF");
			return;
		}

		setLoading(true);
		setPdfError("");

		try {
			const formPayload = new FormData();
			formPayload.append("materia_id", formData.materia_id);
			formPayload.append("nota_aprobacion", nota);
			formPayload.append("justificacion", formData.justificacion.trim());
			formPayload.append("sede_preferencia", formData.sede_preferencia);
			formPayload.append("modalidad", formData.modalidad);
			formPayload.append("archivo_pdf", archivoPdf);

			await axiosInstance.post("/postulaciones", formPayload);

			toast.success("Postulación enviada correctamente");
			setFormData({
				materia_id: "",
				justificacion: "",
				nota_aprobacion: "",
				sede_preferencia: "",
				modalidad: "",
			});
			setArchivoPdf(null);
			if (fileInputRef.current) fileInputRef.current.value = "";
			fetchApplications();
		} catch (error) {
			console.error("Error al enviar la postulación:", error);
			const msg = error.response?.data || "Error al enviar la postulación";
			toast.error(typeof msg === "string" ? msg : JSON.stringify(msg));
		} finally {
			setLoading(false);
		}
	};

	const handleDelete = async (id) => {
		if (!confirm("¿Estás seguro de que deseas cancelar esta postulación?")) return;
		try {
			await axiosInstance.delete(`/postulaciones/${id}`);
			toast.success("Postulación cancelada");
			fetchApplications();
		} catch (error) {
			console.error("Error al eliminar postulación:", error);
			const msg = error.response?.data || "Error al eliminar la postulación";
			toast.error(typeof msg === "string" ? msg : JSON.stringify(msg));
		}
	};

	const getStatusBadge = (status) => {
		switch (status) {
			case "pendiente":
				return (
					<Badge variant="secondary" className="gap-1">
						<Clock className="h-3 w-3" />
						Pendiente
					</Badge>
				);
			case "aprobada":
			case "aprobado":
				return (
					<Badge className="bg-green-600 gap-1 text-white hover:bg-green-700">
						<CheckCircle className="h-3 w-3" />
						Aprobado
					</Badge>
				);
			case "rechazada":
			case "rechazado":
				return (
					<Badge variant="destructive" className="gap-1">
						<XCircle className="h-3 w-3" />
						Rechazado
					</Badge>
				);
			default:
				return <Badge variant="outline">{status}</Badge>;
		}
	};

	return (
		<div>
			<div className="container mx-auto space-y-6 p-6">
				<div>
					<h1 className="text-3xl font-bold">Postulación como Tutor</h1>
					<p className="text-muted-foreground mt-2">Postúlate para ser tutor en las materias que hayas aprobado</p>
				</div>

				{/* Benefits */}
				<div className="grid gap-4 sm:grid-cols-3">
					<Card>
						<CardContent className="pt-6">
							<div className="flex items-center gap-3">
								<div className="flex h-10 w-10 items-center justify-center rounded-lg bg-sky-100">
									<Award className="h-5 w-5 text-sky-600" />
								</div>
								<div>
									<h4 className="font-medium">Certificados</h4>
									<p className="text-sm text-muted-foreground">Obtén certificados oficiales</p>
								</div>
							</div>
						</CardContent>
					</Card>
					<Card>
						<CardContent className="pt-6">
							<div className="flex items-center gap-3">
								<div className="flex h-10 w-10 items-center justify-center rounded-lg bg-sky-100">
									<Users className="h-5 w-5 text-sky-600" />
								</div>
								<div>
									<h4 className="font-medium">Ayuda a otros</h4>
									<p className="text-sm text-muted-foreground">Comparte tu conocimiento</p>
								</div>
							</div>
						</CardContent>
					</Card>
					<Card>
						<CardContent className="pt-6">
							<div className="flex items-center gap-3">
								<div className="flex h-10 w-10 items-center justify-center rounded-lg bg-sky-100">
									<GraduationCap className="h-5 w-5 text-sky-600" />
								</div>
								<div>
									<h4 className="font-medium">Experiencia</h4>
									<p className="text-sm text-muted-foreground">Desarrolla habilidades</p>
								</div>
							</div>
						</CardContent>
					</Card>
				</div>

				<div className="grid gap-6 lg:grid-cols-2">
					{/* Application Form */}
					<Card>
						<CardHeader>
							<CardTitle>Nueva Postulación</CardTitle>
							<CardDescription>Completa el formulario para postularte como tutor</CardDescription>
						</CardHeader>
						<CardContent>
							{loadingMaterias ? (
								<div className="flex items-center justify-center py-8">
									<Loader2 className="h-6 w-6 animate-spin text-muted-foreground" />
								</div>
							) : availableMaterias.length === 0 ? (
								<Alert>
									<AlertCircle className="h-4 w-4" />
									<AlertDescription>
										Ya te has postulado para todas las materias disponibles o no hay materias en el sistema.
									</AlertDescription>
								</Alert>
							) : (
								<form onSubmit={handleSubmit} className="space-y-4">
									<div className="space-y-2">
										<Label htmlFor="materia">Materia</Label>
										<Select value={formData.materia_id} onValueChange={(value) => setFormData({ ...formData, materia_id: value })}>
											<SelectTrigger>
												<BookOpen className="h-4 w-4 mr-2 text-muted-foreground" />
												<SelectValue placeholder="Selecciona una materia" />
											</SelectTrigger>
											<SelectContent>
												{availableMaterias.map((materia) => (
													<SelectItem key={materia.id} value={String(materia.id)}>
														{materia.nombre}
													</SelectItem>
												))}
											</SelectContent>
										</Select>
									</div>

									<div className="space-y-2">
										<Label htmlFor="nota">Nota con la que aprobaste (4-10)</Label>
										<Input
											id="nota"
											type="number"
											min="4"
											max="10"
											step="0.5"
											placeholder="Ej: 7.5"
											value={formData.nota_aprobacion}
											onChange={(e) => setFormData({ ...formData, nota_aprobacion: e.target.value })}
											required
										/>
									</div>

									<div className="space-y-2">
										<Label htmlFor="justificacion">¿Por qué quieres ser tutor de esta materia?</Label>
										<Textarea
											id="justificacion"
											placeholder="Describe tu experiencia con la materia, por qué te gustaría ayudar a otros estudiantes, y cualquier experiencia relevante que tengas..."
											value={formData.justificacion}
											onChange={(e) => setFormData({ ...formData, justificacion: e.target.value })}
											rows={4}
											required
										/>
										<p className="text-xs text-muted-foreground">
											{formData.justificacion.length}/50 caracteres mínimo. Sé específico sobre tu experiencia.
										</p>
									</div>

									<div className="space-y-2">
										<Label htmlFor="sede">Sede Preferida</Label>
										<Select value={formData.sede_preferencia} onValueChange={(value) => setFormData({ ...formData, sede_preferencia: value })}>
											<SelectTrigger>
												<SelectValue placeholder="Selecciona una sede" />
											</SelectTrigger>
											<SelectContent>
												<SelectItem value="Pergamino">Pergamino</SelectItem>
												<SelectItem value="Junín">Junín</SelectItem>
											</SelectContent>
										</Select>
									</div>

									<div className="space-y-2">
										<Label htmlFor="modalidad">Modalidad Preferida</Label>
										<Select value={formData.modalidad} onValueChange={(value) => setFormData({ ...formData, modalidad: value })}>
											<SelectTrigger>
												<SelectValue placeholder="Selecciona una modalidad" />
											</SelectTrigger>
											<SelectContent>
												<SelectItem value="PRESENCIAL">Presencial</SelectItem>
												<SelectItem value="VIRTUAL">Virtual</SelectItem>
												<SelectItem value="HIBRIDA">Híbrida</SelectItem>
											</SelectContent>
										</Select>
									</div>

									{/* PDF Upload */}
									<div className="space-y-2">
										<Label>Analítico (PDF)</Label>
										<div
											className="relative flex flex-col items-center justify-center rounded-lg border-2 border-dashed border-sky-300 bg-sky-50/50 p-6 transition-colors hover:border-sky-400 hover:bg-sky-50 cursor-pointer"
											onClick={() => fileInputRef.current?.click()}
										>
											<Input
												id="archivo"
												type="file"
												ref={fileInputRef}
												accept=".pdf,application/pdf"
												onChange={(e) => {
													const file = e.target.files[0];
													if (file) {
														if (file.type !== "application/pdf") {
															setPdfError("El archivo debe ser un PDF");
															setArchivoPdf(null);
														} else {
															setArchivoPdf(file);
															setPdfError("");
														}
													}
												}}
												className="hidden"
											/>
											{archivoPdf ? (
												<div className="flex items-center gap-3 w-full" onClick={(e) => e.stopPropagation()}>
													<div className="flex h-12 w-12 items-center justify-center rounded-lg bg-sky-100 shrink-0">
														<FileText className="h-6 w-6 text-sky-600" />
													</div>
													<div className="flex-1 min-w-0">
														<p className="text-sm font-medium text-sky-700 truncate">{archivoPdf.name}</p>
														<p className="text-xs text-muted-foreground">{(archivoPdf.size / 1024).toFixed(1)} KB</p>
													</div>
													<Button
														type="button"
														variant="ghost"
														size="icon"
														className="h-8 w-8 shrink-0 text-muted-foreground hover:text-red-600"
														onClick={(e) => {
															e.stopPropagation();
															setArchivoPdf(null);
															setPdfError("");
															if (fileInputRef.current) fileInputRef.current.value = "";
														}}
													>
														<Trash2 className="h-4 w-4" />
													</Button>
												</div>
											) : (
												<>
													<Upload className="h-8 w-8 text-sky-400 mb-2" />
													<p className="text-sm font-medium text-sky-600">Haz clic para seleccionar tu analítico</p>
													<p className="text-xs text-muted-foreground mt-1">Solo archivos PDF</p>
												</>
											)}
										</div>
										{pdfError && <p className="text-xs text-red-500">{pdfError}</p>}
									</div>

									<Button
										type="submit"
										className="w-full bg-sky-500 hover:bg-sky-600 text-white"
										disabled={loading || formData.justificacion.length < 50 || !formData.materia_id || !formData.nota_aprobacion || !formData.sede_preferencia || !formData.modalidad || !archivoPdf}
									>
										{loading ? <Loader2 className="h-4 w-4 animate-spin mr-2" /> : <Send className="h-4 w-4 mr-2" />}
										Enviar Postulación
									</Button>
								</form>
							)}
						</CardContent>
					</Card>

					{/* Applications List */}
					<Card>
						<CardHeader>
							<CardTitle>Mis Postulaciones</CardTitle>
							<CardDescription>Historial de tus postulaciones como tutor</CardDescription>
						</CardHeader>
						<CardContent>
							{loadingApps ? (
								<div className="flex items-center justify-center py-8">
									<Loader2 className="h-6 w-6 animate-spin text-muted-foreground" />
								</div>
							) : applications.length > 0 ? (
								<div className="space-y-4">
									{applications.map((application) => (
										<div key={application.id} className="rounded-lg border p-4 space-y-3">
											<div className="flex items-start justify-between">
												<div>
													<h4 className="font-medium">{application.materia?.nombre}</h4>
													<p className="text-sm text-muted-foreground">Nota: {application.nota_aprobacion}</p>
												</div>
												<div className="flex items-center gap-2 shrink-0">
													{getStatusBadge(application.status)}
													{application.status === "pendiente" && (
														<Button
															type="button"
															variant="ghost"
															size="icon"
															className="h-7 w-7 text-muted-foreground hover:text-red-600"
															onClick={() => handleDelete(application.id)}
															title="Cancelar postulación"
														>
															<XCircle className="h-4 w-4" />
														</Button>
													)}
												</div>
											</div>
											<p className="text-sm text-muted-foreground line-clamp-2">{application.justificacion}</p>
											{application.admin_comentario && (
												<div className="rounded bg-muted p-2">
													<p className="text-xs font-medium">Comentario del administrador:</p>
													<p className="text-sm text-muted-foreground">{application.admin_comentario}</p>
												</div>
											)}
											<p className="text-xs text-muted-foreground">
												Enviada el {format(new Date(application.created_at), "d 'de' MMMM, yyyy", { locale: es })}
											</p>
										</div>
									))}
								</div>
							) : (
								<div className="flex flex-col items-center justify-center py-8 text-center">
									<GraduationCap className="h-12 w-12 text-muted-foreground/50 mb-4" />
									<p className="text-muted-foreground">No tienes postulaciones</p>
									<p className="text-sm text-muted-foreground">Envía tu primera postulación para ser tutor</p>
								</div>
							)}
						</CardContent>
					</Card>
				</div>
			</div>
		</div>
	);
}

export default PostulacionTutorClient;
