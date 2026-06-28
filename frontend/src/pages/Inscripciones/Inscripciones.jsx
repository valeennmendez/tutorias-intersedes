import { useState, useEffect, useCallback } from "react";
import { Link } from "react-router-dom";
import { Card, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import { Checkbox } from "@/components/ui/checkbox";
import {
  Calendar,
  Clock,
  MapPin,
  Video,
  Star,
  Loader2,
  MessageSquare,
  CalendarCheck,
  CalendarX,
  History,
} from "lucide-react";
import { format } from "date-fns";
import { es } from "date-fns/locale";
import toast from "react-hot-toast";
import { axiosInstance } from "@/utils/axios";

export function MisInscripciones() {
  const [inscripciones, setInscripciones] = useState([]);
  const [cargando, setCargando] = useState(true);
  const [historial, setHistorial] = useState([]);
  const [cargandoHistorial, setCargandoHistorial] = useState(false);
  const [loading, setLoading] = useState(null);
  const [cancelDialogOpen, setCancelDialogOpen] = useState(false);
  const [cancelTutoriaId, setCancelTutoriaId] = useState(null);
  const [reactivarDialogOpen, setReactivarDialogOpen] = useState(false);
  const [reactivarTutoriaId, setReactivarTutoriaId] = useState(null);
  const [feedbackDialogOpen, setFeedbackDialogOpen] = useState(false);
  const [selectedInscripcion, setSelectedInscripcion] = useState(null);
  const [feedbackData, setFeedbackData] = useState({
    calificacion: 5,
    comentario: "",
    es_anonimo: false,
  });

  const cargarInscripciones = useCallback(async () => {
    setCargando(true);
    try {
      const res = await axiosInstance.get("/inscripciones/mis-inscripciones");
      setInscripciones(res.data);
    } catch {
      toast.error("No se pudieron cargar las inscripciones");
    } finally {
      setCargando(false);
    }
  }, []);

  const cargarHistorial = useCallback(async () => {
    setCargandoHistorial(true);
    try {
      const res = await axiosInstance.get("/inscripciones/historial");
      setHistorial(res.data);
    } catch {
      toast.error("No se pudo cargar el historial");
    } finally {
      setCargandoHistorial(false);
    }
  }, []);

  useEffect(() => {
    cargarInscripciones();
    cargarHistorial();
  }, [cargarInscripciones, cargarHistorial]);

  const activasInscripciones = inscripciones.filter(
    (i) => i.status === "ACTIVA"
  );

  const canceladasInscripciones = inscripciones.filter(
    (i) => i.status === "CANCELADA"
  );

  const openCancelDialog = (tutoriaId) => {
    setCancelTutoriaId(tutoriaId);
    setCancelDialogOpen(true);
  };

  const confirmCancel = async () => {
    if (!cancelTutoriaId) return;
    setLoading(cancelTutoriaId);
    setCancelDialogOpen(false);
    try {
      await axiosInstance.put(`/inscripciones/tutoria/${cancelTutoriaId}/cancelar`);

      toast.success("Inscripción cancelada");
      await cargarInscripciones();
    } catch {
      toast.error("Error al cancelar la inscripción");
    } finally {
      setLoading(null);
      setCancelTutoriaId(null);
    }
  };

  const openReactivarDialog = (tutoriaId) => {
    setReactivarTutoriaId(tutoriaId);
    setReactivarDialogOpen(true);
  };

  const confirmReactivar = async () => {
    if (!reactivarTutoriaId) return;
    setLoading(reactivarTutoriaId);
    setReactivarDialogOpen(false);
    try {
      await axiosInstance.post(`/inscripciones/tutoria/${reactivarTutoriaId}`);

      toast.success("Inscripción reactivada");
      await cargarInscripciones();
    } catch (error) {
      if (error.response?.status === 409) {
        toast.error("No hay cupos disponibles");
        return;
      }
      toast.error("Error al reactivar la inscripción");
    } finally {
      setLoading(null);
      setReactivarTutoriaId(null);
    }
  };

  const handleSubmitFeedback = async () => {
    if (!selectedInscripcion) return;

    setLoading("feedback");
    try {
      await axiosInstance.post("/feedback", {
        inscripcion_id: selectedInscripcion,
        calificacion: feedbackData.calificacion,
        comentario: feedbackData.comentario || null,
        es_anonimo: feedbackData.es_anonimo,
      });

      toast.success("¡Gracias por tu feedback!");
      setFeedbackDialogOpen(false);
      setSelectedInscripcion(null);
      setFeedbackData({ calificacion: 5, comentario: "", es_anonimo: false });
      await cargarInscripciones();
    } catch (error) {
      if (error.response?.status === 409) {
        toast.error("Ya has dejado feedback para esta tutoría");
        return;
      }
      toast.error("Error al enviar feedback");
    } finally {
      setLoading(null);
    }
  };

  const openFeedbackDialog = (inscripcionId) => {
    setSelectedInscripcion(inscripcionId);
    setFeedbackDialogOpen(true);
  };

  const statusBadge = (status) => {
    if (status === "ACTIVA") {
      return <Badge className="bg-green-600">Activa</Badge>;
    }
    if (status === "CANCELADA") {
      return <Badge variant="destructive">Cancelada</Badge>;
    }
    return <Badge variant="outline">{status}</Badge>;
  };

  const InscripcionCard = ({ inscripcion, showActions = false }) => (
    <Card>
      <CardContent className="pt-6">
        <div className="flex flex-col gap-4 sm:flex-row sm:items-start sm:justify-between">
          <div className="space-y-2 flex-1">
            <div className="flex items-center gap-2 flex-wrap">
              <h4 className="font-medium">{inscripcion.nombreTutoria}</h4>
              {statusBadge(inscripcion.status)}
            </div>
            <p className="text-sm text-muted-foreground">
              {inscripcion.nombreTutor}
            </p>
            {inscripcion.fechaInscripcion && (
              <div className="flex items-center gap-4 text-sm text-muted-foreground">
                <span className="flex items-center gap-1">
                  <Calendar className="h-4 w-4" />
                  Inscripto el{" "}
                  {format(new Date(inscripcion.fechaInscripcion), "d 'de' MMMM, yyyy", {
                    locale: es,
                  })}
                </span>
              </div>
            )}
          </div>
          {showActions && (
            <div className="flex gap-2 sm:flex-col">
              {inscripcion.status === "ACTIVA" && (
                <Button
                  variant="destructive"
                  size="sm"
                  onClick={() => openCancelDialog(inscripcion.tutoriaId)}
                  disabled={loading === inscripcion.tutoriaId}
                >
                  {loading === inscripcion.tutoriaId ? (
                    <Loader2 className="h-4 w-4 animate-spin" />
                  ) : (
                    "Cancelar"
                  )}
                </Button>
              )}
              {inscripcion.status === "CANCELADA" && (
                <Button
                  variant="outline"
                  size="sm"
                  onClick={() => openReactivarDialog(inscripcion.tutoriaId)}
                  disabled={loading === inscripcion.tutoriaId}
                >
                  {loading === inscripcion.tutoriaId ? (
                    <Loader2 className="h-4 w-4 animate-spin" />
                  ) : (
                    "Reactivar"
                  )}
                </Button>
              )}
              {inscripcion.status === "ACTIVA" && (
                <Button size="sm" onClick={() => openFeedbackDialog(inscripcion.id)}>
                  <MessageSquare className="h-4 w-4 mr-1" />
                  Dejar feedback
                </Button>
              )}
              <Button variant="outline" size="sm" asChild>
                <Link to={`/dashboard/tutorias/${inscripcion.tutoriaId}`}>
                  Ver detalles
                </Link>
              </Button>
            </div>
          )}
        </div>
      </CardContent>
    </Card>
  );

  if (cargando) {
    return (
      <div className="flex justify-center py-12">
        <Loader2 className="h-8 w-8 animate-spin text-muted-foreground" />
      </div>
    );
  }

  return (
    <div className="container mx-auto space-y-6 p-6">
      <div>
        <h1 className="text-2xl font-bold text-foreground">Mis Inscripciones</h1>
        <p className="text-muted-foreground">
          Gestiona tus inscripciones a tutorías
        </p>
      </div>

      <Tabs defaultValue="activas" className="space-y-4">
        <TabsList>
          <TabsTrigger value="activas" className="gap-2">
            <CalendarCheck className="h-4 w-4" />
            Activas ({activasInscripciones.length})
          </TabsTrigger>
          <TabsTrigger value="canceladas" className="gap-2">
            <CalendarX className="h-4 w-4" />
            Canceladas ({canceladasInscripciones.length})
          </TabsTrigger>
          <TabsTrigger value="historial" className="gap-2">
            <History className="h-4 w-4" />
            Historial ({historial.length})
          </TabsTrigger>
        </TabsList>

        <TabsContent value="activas" className="space-y-4">
          {activasInscripciones.length > 0 ? (
            activasInscripciones.map((inscripcion) => (
              <InscripcionCard key={inscripcion.id} inscripcion={inscripcion} showActions />
            ))
          ) : (
            <Card>
              <CardContent className="flex flex-col items-center justify-center py-12">
                <CalendarCheck className="h-16 w-16 text-muted-foreground/50 mb-4" />
                <h3 className="text-lg font-medium mb-2">
                  No tienes inscripciones activas
                </h3>
                <p className="text-muted-foreground text-center mb-4">
                  Explora las tutorías disponibles y anótate
                </p>
                <Button asChild>
                  <Link to="/dashboard/tutorias">Ver tutorías disponibles</Link>
                </Button>
              </CardContent>
            </Card>
          )}
        </TabsContent>

        <TabsContent value="canceladas" className="space-y-4">
          {canceladasInscripciones.length > 0 ? (
            canceladasInscripciones.map((inscripcion) => (
              <InscripcionCard key={inscripcion.id} inscripcion={inscripcion} showActions />
            ))
          ) : (
            <Card>
              <CardContent className="flex flex-col items-center justify-center py-12">
                <CalendarX className="h-16 w-16 text-muted-foreground/50 mb-4" />
                <h3 className="text-lg font-medium mb-2">
                  No hay inscripciones canceladas
                </h3>
                <p className="text-muted-foreground text-center">
                  Las inscripciones que canceles aparecerán aquí
                </p>
              </CardContent>
            </Card>
          )}
        </TabsContent>

        <TabsContent value="historial" className="space-y-4">
          {cargandoHistorial ? (
            <div className="flex justify-center py-12">
              <Loader2 className="h-8 w-8 animate-spin text-muted-foreground" />
            </div>
          ) : historial.length > 0 ? (
            historial.map((item) => (
              <Card key={item.inscripcionId}>
                <CardContent className="pt-6">
                  <div className="flex flex-col gap-4 sm:flex-row sm:items-start sm:justify-between">
                    <div className="space-y-2 flex-1">
                      <div className="flex items-center gap-2 flex-wrap">
                        <h4 className="font-medium">{item.nombreTutoria}</h4>
                        <Badge variant="outline">
                          {item.modalidad === "VIRTUAL" ? (
                            <Video className="h-3 w-3 mr-1" />
                          ) : (
                            <MapPin className="h-3 w-3 mr-1" />
                          )}
                          {item.modalidad}
                        </Badge>
                      </div>
                      <p className="text-sm text-muted-foreground">
                        {item.materiaNombre} - {item.nombreTutor}
                      </p>
                      <div className="flex items-center gap-4 text-sm text-muted-foreground">
                        <span className="flex items-center gap-1">
                          <Calendar className="h-4 w-4" />
                          {format(new Date(item.fecha), "d 'de' MMMM, yyyy", {
                            locale: es,
                          })}
                        </span>
                        <span className="flex items-center gap-1">
                          <Clock className="h-4 w-4" />
                          {item.horaInicio.slice(0, 5)} - {item.horaFin.slice(0, 5)}
                        </span>
                        <span className="flex items-center gap-1">
                          <MapPin className="h-4 w-4" />
                          {item.sede}
                        </span>
                      </div>
                    </div>
                  </div>
                </CardContent>
              </Card>
            ))
          ) : (
            <Card>
              <CardContent className="flex flex-col items-center justify-center py-12">
                <History className="h-16 w-16 text-muted-foreground/50 mb-4" />
                <h3 className="text-lg font-medium mb-2">
                  No tienes tutorías en el historial
                </h3>
                <p className="text-muted-foreground text-center">
                  Aquí aparecerán las tutorías finalizadas a las que te inscribiste
                </p>
              </CardContent>
            </Card>
          )}
        </TabsContent>
      </Tabs>

      <Dialog open={reactivarDialogOpen} onOpenChange={setReactivarDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Reactivar inscripción</DialogTitle>
            <DialogDescription>
              ¿Estás seguro que deseas reactivar esta inscripción?
            </DialogDescription>
          </DialogHeader>
          <DialogFooter>
            <Button variant="outline" onClick={() => setReactivarDialogOpen(false)}>
              No, mantener
            </Button>
            <Button variant="default" onClick={confirmReactivar}>
              Sí, reactivar
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      <Dialog open={cancelDialogOpen} onOpenChange={setCancelDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Cancelar inscripción</DialogTitle>
            <DialogDescription>
              ¿Estás seguro que deseas cancelar esta inscripción?
            </DialogDescription>
          </DialogHeader>
          <DialogFooter>
            <Button variant="outline" onClick={() => setCancelDialogOpen(false)}>
              No, mantener
            </Button>
            <Button variant="destructive" onClick={confirmCancel}>
              Sí, cancelar
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      <Dialog open={feedbackDialogOpen} onOpenChange={setFeedbackDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Dejar Feedback</DialogTitle>
            <DialogDescription>
              Tu opinión ayuda a mejorar las tutorías y a otros estudiantes a elegir
            </DialogDescription>
          </DialogHeader>
          <div className="space-y-4 py-4">
            <div className="space-y-2">
              <Label>Calificación</Label>
              <div className="flex gap-1">
                {Array.from({ length: 5 }).map((_, i) => (
                  <button
                    key={i}
                    type="button"
                    onClick={() =>
                      setFeedbackData({ ...feedbackData, calificacion: i + 1 })
                    }
                    className="p-1 hover:scale-110 transition-transform"
                  >
                    <Star
                      className={`h-8 w-8 ${
                        i < feedbackData.calificacion
                          ? "fill-primary text-primary"
                          : "text-muted-foreground"
                      }`}
                    />
                  </button>
                ))}
              </div>
            </div>
            <div className="space-y-2">
              <Label htmlFor="comentario">Comentario (opcional)</Label>
              <Textarea
                id="comentario"
                placeholder="¿Qué te pareció la tutoría?"
                value={feedbackData.comentario}
                onChange={(e) =>
                  setFeedbackData({ ...feedbackData, comentario: e.target.value })
                }
                rows={3}
              />
            </div>
            <div className="flex items-center space-x-2">
              <Checkbox
                id="anonimo"
                checked={feedbackData.es_anonimo}
                onCheckedChange={(checked) =>
                  setFeedbackData({ ...feedbackData, es_anonimo: Boolean(checked) })
                }
              />
              <Label htmlFor="anonimo" className="text-sm font-normal">
                Publicar como anónimo
              </Label>
            </div>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setFeedbackDialogOpen(false)}>
              Cancelar
            </Button>
            <Button onClick={handleSubmitFeedback} disabled={loading === "feedback"}>
              {loading === "feedback" ? (
                <Loader2 className="h-4 w-4 animate-spin mr-2" />
              ) : null}
              Enviar feedback
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}