#ifndef AUDIO_H
#define AUDIO_H

#include <QObject>
#include <QQmlEngine>

class Audio : public QObject
{
    Q_OBJECT
    QML_ELEMENT
public:
    explicit Audio(QObject *parent = nullptr);

public slots:
    void onClicked();
};

#endif
