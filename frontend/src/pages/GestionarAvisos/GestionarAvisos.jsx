import { useState, useEffect } from "react";
import { useAuthStore } from "@/store/auth.store";
import { useAvisosStore } from "@/store/avisos.store";
import { axiosInstance } from "@/utils/axios";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { Badge } from "@/components/ui/badge";
import { Bell, Send, Trash2, BookOpen, Calendar, Clock, Loader2 } from "lucide-react";
import { format } from "date-fns";
import { es } from "date-fns/locale";

export default function GestionarAvisos() {
	const { user } = useAuthStore();
	const { crearAviso, getMisAvisosEnviados, eliminarAviso, avisos, loading } = useAvisosStore();

	const [tutorias, setTutorias] = useState([]);
	const [selectedTutoriaId, setSelectedTutoriaId] = useState("");
	const [titulo, setTitulo] = useState("");
	const [contenido, setContenido] = useState("");
	const [submitting, setSubmitting] = useState(false);

	useEffect(() => {
		const fetchData = async () => {
			try {
				const [tutoriasRes] = await Promise.all([
					axiosInstance.get("/tutorias"),
					getMisAvisosEnviados(),
				]);
				const misTutorias = tutoriasRes.data?.filter((t) => t.tutorId === user?.id) || [];
				setTutorias(misTutorias);
			} catch (error) {
				console.error("Error al cargar datos:", error);
			}
		};
		fetchData();
	}, [user]);

	const handleCrearAviso = async (e) => {
		e.preventDefault();
		if (!selectedTutoriaId || !titulo.trim() || !contenido.trim()) {
			return;
		}
		setSubmitting(true);
		const nuevo = await crearAviso({
			titulo: titulo.trim(),
			contenido: contenido.trim(),
			tutoriaId: parseInt(selectedTutoriaId),
		});
		if (nuevo) {
			setTitulo("");
			setContenido("");
			setSelectedTutoriaId("");
			getMisAvisosEnviados();
		}
		setSubmitting(false);
	};

	const handleEliminar = async (id) => {
		const ok = await eliminarAviso(id);
		if (ok) {
			getMisAvisosEnviados();
		}
	};

	return (
		<div className="container mx-auto space-y-6 p-6">
			<div>
				<h1 className="text-2xl font-bold text-foreground">Gestionar Avisos</h1>
				<p className="text-muted-foreground">Creá y administrá los avisos de tus tutorías</p>
			</div>

			<div className="grid gap-6 lg:grid-cols-2">
				<Card>
					<CardHeader>
						<div className="flex items-center gap-2">
							<Send className="h-5 w-5 text-primary" />
							<CardTitle className="text-lg">Nuevo Aviso</CardTitle>
						</div>
						<CardDescription>Enviale un aviso a los alumnos de una tutoría</CardDescription>
					</CardHeader>
					<CardContent>
						<form onSubmit={handleCrearAviso} className="space-y-4">
							<div className="space-y-2">
								<label className="text-sm font-medium">Tutoría</label>
								{tutorias.length === 0 ? (
									<p className="text-sm text-muted-foreground">No tenés tutorías creadas</p>
								) : (
									<select
										value={selectedTutoriaId}
										onChange={(e) => setSelectedTutoriaId(e.target.value)}
										className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2"
										required
									>
										<option value="" disabled>Seleccioná una tutoría</option>
										{tutorias.map((t) => (
											<option key={t.id} value={t.id}>
												{t.nombre} — {t.materiaNombre} ({format(new Date(t.fecha), "d/M/yyyy", { locale: es })})
											</option>
										))}
									</select>
								)}
							</div>
							<div className="space-y-2">
								<label className="text-sm font-medium">Título</label>
								<Input
									value={titulo}
									onChange={(e) => setTitulo(e.target.value)}
									placeholder="Ej: Material de estudio"
									required
								/>
							</div>
							<div className="space-y-2">
								<label className="text-sm font-medium">Contenido</label>
								<Textarea
									value={contenido}
									onChange={(e) => setContenido(e.target.value)}
									placeholder="Escribí el mensaje para los alumnos..."
									required
								/>
							</div>
							<Button
								type="submit"
								className="bg-sky-500 hover:bg-sky-600 text-white"
								disabled={submitting || tutorias.length === 0}
							>
								{submitting ? (
									<><Loader2 className="mr-2 h-4 w-4 animate-spin" /> Enviando...</>
								) : (
									<><Send className="mr-2 h-4 w-4" /> Publicar Aviso</>
								)}
							</Button>
						</form>
					</CardContent>
				</Card>

				<Card>
					<CardHeader>
						<div className="flex items-center gap-2">
							<Bell className="h-5 w-5 text-primary" />
							<CardTitle className="text-lg">Avisos Enviados</CardTitle>
						</div>
						<CardDescription>Últimos avisos que publicaste</CardDescription>
					</CardHeader>
					<CardContent>
						{loading ? (
							<div className="flex justify-center py-8">
								<Loader2 className="h-6 w-6 animate-spin text-muted-foreground" />
							</div>
						) : avisos.length > 0 ? (
							<div className="space-y-3">
								{avisos.map((aviso) => (
									<div
										key={aviso.id}
										className="rounded-lg border p-4 space-y-2"
									>
										<div className="flex items-start justify-between gap-2">
											<div>
												<h4 className="font-medium text-sm">{aviso.titulo}</h4>
												<p className="text-xs text-muted-foreground line-clamp-2">{aviso.contenido}</p>
											</div>
											<Button
												variant="ghost"
												size="icon"
												className="shrink-0 text-destructive hover:text-destructive"
												onClick={() => handleEliminar(aviso.id)}
											>
												<Trash2 className="h-4 w-4" />
											</Button>
										</div>
										<div className="flex flex-wrap items-center gap-x-4 gap-y-1 text-xs text-muted-foreground">
											<span className="flex items-center gap-1">
												<Calendar className="h-3 w-3" />
												{format(new Date(aviso.fechaCreacion), "d MMM yyyy", { locale: es })}
											</span>
											<span className="flex items-center gap-1">
												<BookOpen className="h-3 w-3" />
												{aviso.nombreTutoria}
											</span>
										</div>
									</div>
								))}
							</div>
						) : (
							<div className="flex flex-col items-center justify-center py-8 text-center">
								<Bell className="h-8 w-8 text-muted-foreground/50 mb-2" />
								<p className="text-sm text-muted-foreground">No enviaste avisos todavía</p>
							</div>
						)}
					</CardContent>
				</Card>
			</div>
		</div>
	);
}
