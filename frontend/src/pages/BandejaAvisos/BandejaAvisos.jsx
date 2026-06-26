import { useEffect } from "react";
import { useAvisosStore } from "@/store/avisos.store";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Bell, BookOpen, User, Calendar, Loader2 } from "lucide-react";
import { format } from "date-fns";
import { es } from "date-fns/locale";

export default function BandejaAvisos() {
	const { getMisAvisos, avisos, loading } = useAvisosStore();

	useEffect(() => {
		getMisAvisos();
	}, []);

	return (
		<div className="container mx-auto space-y-6 p-6">
			<div>
				<h1 className="text-2xl font-bold text-foreground">Bandeja de Avisos</h1>
				<p className="text-muted-foreground">Todos los avisos de tus tutorías</p>
			</div>

			<Card>
				<CardHeader>
					<div className="flex items-center gap-2">
						<Bell className="h-5 w-5 text-primary" />
						<CardTitle className="text-lg">Avisos</CardTitle>
					</div>
					<CardDescription>Notificaciones de los tutores de tus materias</CardDescription>
				</CardHeader>
				<CardContent>
					{loading ? (
						<div className="flex justify-center py-8">
							<Loader2 className="h-6 w-6 animate-spin text-muted-foreground" />
						</div>
					) : avisos.length > 0 ? (
						<div className="space-y-4">
							{avisos.map((aviso) => (
								<div
									key={aviso.id}
									className="rounded-lg border p-4 space-y-2"
								>
									<div className="flex items-start justify-between gap-2">
										<div className="space-y-1">
											<h4 className="font-medium">{aviso.titulo}</h4>
											<p className="text-sm text-muted-foreground">{aviso.contenido}</p>
										</div>
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
										<span className="flex items-center gap-1">
											<User className="h-3 w-3" />
											{aviso.nombreTutor}
										</span>
									</div>
								</div>
							))}
						</div>
					) : (
						<div className="flex flex-col items-center justify-center py-12 text-center">
							<Bell className="h-12 w-12 text-muted-foreground/50 mb-4" />
							<p className="text-muted-foreground">No tenés avisos</p>
							<p className="text-sm text-muted-foreground">Los tutores publicarán avisos en tus tutorías</p>
						</div>
					)}
				</CardContent>
			</Card>
		</div>
	);
}
